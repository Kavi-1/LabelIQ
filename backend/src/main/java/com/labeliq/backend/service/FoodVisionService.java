package com.labeliq.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.labeliq.backend.dto.FoodAnalysisResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import java.util.Base64;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodVisionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    public FoodAnalysisResult analyzeImage(byte[] imageBytes) {

        String systemPrompt = """
                You are an expert nutritionist and food recognition model.

                The user will provide an image of a single plate / cup / bowl of food.
                Your job is to:
                1. Infer the specific dish (e.g. "pepperoni pizza slice", "chicken burrito", "margherita pizza").
                2. Infer the approximate serving size from what is visible (e.g. "1 slice", "roughly 250g", "medium bowl").
                3. Estimate realistic macro-nutrients for THAT PORTION ONLY using typical values from USDA-style data.

                Return your answer as STRICT JSON ONLY.
                Do NOT include explanations outside the JSON, no prose, no markdown.

                The JSON must have this exact shape:

                {
                  "foodName": "string - short human-readable name of the dish",
                  "servingDescription": "string - how much food is on the plate (e.g. '1 large slice', 'medium bowl')",
                  "calories": 0,
                  "proteinGrams": 0.0,
                  "carbGrams": 0.0,
                  "fatGrams": 0.0,
                  "fiberGrams": 0.0,
                  "confidence": 0.0,
                  "reasoning": "short explanation of how you estimated the macros and any uncertainty"
                }

                Rules:
                - ALWAYS return valid JSON parsable by standard JSON libraries.
                - Use lower-case keys exactly as shown.
                - If you are unsure, still make your best estimate but lower confidence.
                """;

        // Encode image to base64
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        return analyzeWithGemini(systemPrompt, base64Image);
    }

    private FoodAnalysisResult analyzeWithGemini(String systemPrompt, String base64Image) {
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                geminiModel,
                geminiApiKey);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", systemPrompt),
                                        Map.of("inlineData", Map.of(
                                                "mimeType", "image/jpeg",
                                                "data", base64Image))))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return objectMapper.readValue(content, FoodAnalysisResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}
