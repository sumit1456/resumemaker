package com.app.resumemaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    private final WebClient webClient;

    @Value("${huggingface.api.key}")
    private String apiKey;

    public HuggingFaceService(WebClient.Builder webClientBuilder) {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(15 * 1024 * 1024))
                .build();

        this.webClient = webClientBuilder
                .baseUrl("https://router.huggingface.co/v1")
                .exchangeStrategies(strategies)
                .filter(timeoutFilter())
                .filter(rateLimitFilter())
                .build();
    }

    private List<String> splitIntoChunks(String text, int maxSize) {
        List<String> chunks = new ArrayList<>();
        int len = text.length();

        for (int i = 0; i < len; i += maxSize) {
            chunks.add(text.substring(i, Math.min(len, i + maxSize)));
        }
        return chunks;
    }

    private ExchangeFilterFunction rateLimitFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().value() == 429) {
                System.out.println("⚠ Rate limit hit — retrying...");
                return Mono.error(new RuntimeException("RATE_LIMIT"));
            }
            return Mono.just(response);
        });
    }

    private ExchangeFilterFunction timeoutFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(req ->
                Mono.just(req).timeout(Duration.ofSeconds(3))
        );
    }

    public String analyzeResume(String jobDescription, String resumeStateJson) {

        List<String> chunks = splitIntoChunks(resumeStateJson, 3000);
        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content",
                        "You are an expert resume analyzer. Be concise, structured, and job-focused."
        ));

        messages.add(Map.of(
                "role", "user",
                "content", "Job Description:\n" + jobDescription
        ));

        for (int i = 0; i < chunks.size(); i++) {
            messages.add(Map.of(
                    "role", "user",
                    "content", "Resume chunk " + (i + 1) + ":\n" + chunks.get(i)
            ));
        }

        messages.add(Map.of(
                "role", "user",
                "content",
                        "Using ALL chunks, produce a single unified resume analysis:\n" +
                        "- Key matching skills\n" +
                        "- Missing skills\n" +
                        "- Red flags\n" +
                        "- Tone & clarity\n" +
                        "- ATS optimization suggestions\n" +
                        "- Final match score (0–100)\n" +
                        "Do NOT repeat the raw chunks."
        ));

        Map<String, Object> request = Map.of(
                "model", "meta-llama/Llama-3.2-3B-Instruct",   // ✅ free & supported
                "messages", messages,
                "stream", false
        );

        try {
            String responseJson = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(20))
                    .retryWhen(
                            Retry.backoff(3, Duration.ofSeconds(2))
                                    .maxBackoff(Duration.ofSeconds(10))
                                    .filter(err ->
                                            err.getMessage().contains("RATE_LIMIT") ||
                                            err instanceof java.util.concurrent.TimeoutException
                                    )
                    )
                    .block();

            return extract(responseJson);

        } catch (Exception e) {
            return "❌ Request failed: " + e.getMessage();
        }
    }

    public Mono<String> analyzeResumeStream(String jobDescription, String resumeJson) {

        List<String> chunks = splitIntoChunks(resumeJson, 3000);
        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", "You are an expert resume analyzer."
        ));

        messages.add(Map.of(
                "role", "user",
                "content", "Job Description:\n" + jobDescription
        ));

        for (int i = 0; i < chunks.size(); i++) {
            messages.add(Map.of(
                    "role", "user",
                    "content", "Resume chunk " + (i + 1) + ":\n" + chunks.get(i)
            ));
        }

        messages.add(Map.of(
                "role", "user",
                "content", "Give the final combined analysis."
        ));

        Map<String, Object> request = Map.of(
                "model", "meta-llama/Llama-3.2-3B-Instruct",
                "messages", messages,
                "stream", true
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(30))
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(err -> err.getMessage().contains("RATE_LIMIT"))
                )
                .collectList()
                .map(list -> String.join("", list));
    }

    private String extract(String json) {
        try {
            JsonNode root = new ObjectMapper().readTree(json);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "⚠ Failed to parse response.\nRaw: " + json;
        }
    }
}
