package com.app.resumemaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.json.JSONArray;
import org.json.JSONObject;

import reactor.core.publisher.Mono;

@Service
public class GroqAIService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    public GroqAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String analyze(String jobDescription, String resumeJson) {
        try {
            // Build request body
            JSONObject body = new JSONObject();
            body.put("model", model);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You are a resume analysis expert. Compare resume to job description."));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", "Job Description:\n" + jobDescription + "\n\nResume JSON:\n" + resumeJson));

            body.put("messages", messages);
            body.put("temperature", 0.2);
            body.put("max_tokens", 800);

            // Send request
            Mono<String> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body.toString())
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block(); // Blocking call
            return extractContent(response);

        } catch (WebClientResponseException e) {
            return "❌ Groq API Error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "❌ Groq API Error: " + e.getMessage();
        }
    }

    private String extractContent(String responseJson) {
        try {
            JSONObject json = new JSONObject(responseJson);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "❌ Groq returned no choices";
            }
            JSONObject message = choices.getJSONObject(0).optJSONObject("message");
            if (message == null || !message.has("content")) {
                return "❌ Groq response missing content";
            }
            return message.getString("content");
        } catch (Exception e) {
            return "❌ Failed to parse Groq response";
        }
    }
}
