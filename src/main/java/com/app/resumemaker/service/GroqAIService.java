package com.app.resumemaker.service;

import com.app.resumemaker.diff.ResumeComparisonDiff;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.exception.GroqApiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GroqAIService {

    private final WebClient webClient;

    @Value("${groq.api.key2}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model2}")
    private String model;

    public GroqAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String analyze(String jobDescription, String resumeJson) {
        try {
            // Force Groq to produce STRICT JSON only
            String aiPrompt = """
                You are a resume analysis AI.

                Analyze the candidate's resume compared to the job description if given otherwise just analyze in detail. Give multiple points where necessary.  Inside verdict be very strict give ratings

                and write in detail. Return ONLY valid JSON. Do NOT include backticks, markdown, commentary, or text outside JSON.

                JSON STRUCTURE MUST BE EXACTLY:

                {
                  "summary": {
                    "title": "Overall Impression",
                    "points": []
                  },
                  "strengths": [
                    {
                      "area": "",
                      "strong": "",
                      "why": ""
                    }
                  ],
                  "improvements": [
                    {
                      "issue": "",
                      "suggestion": ""
                    }
                  ],
                  "verdict": ""
                }

                Job Description:
                %s

                Resume:
                %s
                """.formatted(jobDescription, resumeJson);

            JSONObject body = new JSONObject();
            body.put("model", model);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You must return only JSON. Never return text outside JSON."));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", aiPrompt));

            body.put("messages", messages);
            body.put("temperature", 0.2);
            body.put("max_tokens", 1200);

            // Call Groq API
            Mono<String> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body.toString())
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();

            // Extract the "content" field of the completion response
            String content = extractContent(response);

            return content; // <- raw JSON string returned by AI

        } catch (WebClientResponseException e) {
            return "{ \"error\": \"Groq API Error: " + e.getResponseBodyAsString() + "\" }";
        } catch (Exception e) {
            return "{ \"error\": \"Groq API Error: " + e.getMessage() + "\" }";
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

    // ---------------------------------------------------------------------
    // ENHANCE RESUME (HttpClient)
    // ---------------------------------------------------------------------
//    public ResumeDTO enhanceResume(ResumeDTO resumeDTO) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//            // Convert ResumeDTO → JSON string
//            String resumeJson = mapper.writeValueAsString(resumeDTO);
//
//            // System prompt
//            String systemPrompt = """
//                You are an advanced ATS resume optimization expert.
//
//                STRICT RULES:
//                - Return ONLY valid JSON.
//                - Start with '{' and end with '}'.
//                - Do NOT add/remove/rename fields.
//                - Do NOT alter array structure.
//                - Do NOT add commentary or explanation.
//                Improve ONLY text fields.
//                """;
//
//            // User prompt
//            String userPrompt = "Enhance this Resume JSON:\n\n" + resumeJson;
//
//            // Build request body
//            JSONObject req = new JSONObject()
//                .put("model", "groq/compound")
//                .put("messages", new JSONArray()
//                    .put(new JSONObject().put("role", "system").put("content", systemPrompt))
//                    .put(new JSONObject().put("role", "user").put("content", userPrompt)));
//
//            // HTTP client config
//            RequestConfig config = RequestConfig.custom()
//                .setConnectionRequestTimeout(Timeout.ofSeconds(60))
//                .setResponseTimeout(Timeout.ofSeconds(60))
//                .build();
//
//            try (CloseableHttpClient client = HttpClients.custom()
//                    .setDefaultRequestConfig(config)
//                    .build()) {
//
//                HttpPost post = new HttpPost("https://api.groq.com/openai/v1/chat/completions");
//                post.setHeader("Authorization", "Bearer " + apiKey);
//                post.setHeader("Content-Type", "application/json");
//                post.setEntity(EntityBuilder.create()
//                        .setText(req.toString())
//                        .setContentType(org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
//                        .build());
//
//                ClassicHttpResponse response = client.execute(post);
//                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//
//                System.out.println("RAW AI RESPONSE:");
//                System.out.println(responseBody);
//
//                // Extract AI "content"
//                JSONObject jsonResponse = new JSONObject(responseBody);
//                
//                if (jsonResponse.has("error")) {
//                    JSONObject err = jsonResponse.getJSONObject("error");
//                    String message = err.optString("message", "Unknown AI error");
//                    String code = err.optString("code", "unknown_error");
//
//                    throw new GroqApiException(
//                            err.optString("message"),
//                            err.optString("code"),
//                            err.optString("type")
//                        );
//                }
//
//                String aiContent = jsonResponse
//                        .getJSONArray("choices")
//                        .getJSONObject(0)
//                        .getJSONObject("message")
//                        .getString("content");
//
//                // Clean response to pure JSON
//                String cleaned = extractPureJson(aiContent);
//               
//
//                // Convert JSON → ResumeDTO
//                return mapper.readValue(cleaned, ResumeDTO.class);
//            }
//
//        }
//        catch(GroqApiException gae) {
//        	throw gae;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("AI parsing failed: " + e.getMessage());
//        }
//    }

    
    
    public String enhanceResumeSimple(ResumeDTO resumeDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String resumeJson = mapper.writeValueAsString(resumeDTO);

            String aiPrompt = """
                You are an advanced ATS resume enhancement AI.

                Enhance the following resume JSON. STRICT RULES:
                - Return ONLY valid JSON.
                - Do NOT change field names, structure, or array hierarchy.
                - Include only these top-level fields: resumeDetails, skills, experiences, projects.
                - If a field is missing or empty, leave it as null or empty array.
                - Do NOT add commentary or text outside JSON.

                Use this as the exact structure reference (your output must match):

                {
                  "resumeDetails": {
                    "name": "",
                    "title": "",
                    "contact": {
                      "phone": "",
                      "email": "",
                      "linkedin": "",
                      "github": "",
                      "location": ""
                    },
                    "summary": ""
                  },
                  "skills": [
                    "Programming Languages - ...",
                    "Databases - ...",
                    "Frameworks & Libraries - ...",
                    "Tools & Platforms - ...",
                    "Cloud & Deployment - ...",
                    "Soft Skills - ..."
                  ],
                  "experiences": [
                    {
                      "title": "",
                      "company": "",
                      "location": "",
                      "startDate": "",
                      "endDate": "",
                      "description": [""]
                    }
                  ],
                  "projects": [
                    {
                      "name": "",
                      "description": [""]
                    }
                  ]
                }

                Enhance this resume JSON:
                %s
                """.formatted(resumeJson);

            JSONObject body = new JSONObject();
            body.put("model", "groq/compound");
            body.put("temperature", 0.2);
            body.put("max_tokens", 3000);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "Return only valid JSON."));
            messages.put(new JSONObject().put("role", "user").put("content", aiPrompt));
            body.put("messages", messages);

            Mono<String> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body.toString())
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();
           
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String enhancedResumeJson = message.getString("content");

            System.out.println("Only the enhanced resume JSON:\n" + enhancedResumeJson);
            return enhancedResumeJson;


        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"AI Resume enhancement failed: " + e.getMessage().replace("\"", "'") + "\" }";
        }
    }



    private String extractPureJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start == -1 || end == -1) return text;
        return text.substring(start, end + 1);
    }

    private ResumeDTO convertJsonToDto(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, ResumeDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert AI JSON into ResumeDTO: " + e.getMessage());
        }
    }

    public ResumeComparisonDiff compareResumes(Map<String, Object> oldResume, Map<String, Object> newResume) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String oldJson = mapper.writeValueAsString(oldResume);
            String newJson = mapper.writeValueAsString(newResume);

            JSONObject body = new JSONObject();
            body.put("model", model);

            JSONArray messages = new JSONArray();

            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", """
                            You are a resume comparison expert.
                            Compare OLD and NEW resumes and return STRICTLY in THIS JSON format:

                            {
                              "summary": { "old": "", "new": "", "changes": [] },
                              "skills": { "old": [], "new": [], "changes": [] },
                              "projects": { "old": [], "new": [], "changes": [] },
                              "education": { "old": [], "new": [], "changes": [] }
                            }

                            - Include only sections that changed.
                            - No markdown. No tables. No commentary.
                            - Only output valid JSON.
                            """));

            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", "OLD:\n" + oldJson + "\n\nNEW:\n" + newJson));

            body.put("messages", messages);
            body.put("max_tokens", 4000);
            body.put("temperature", 0.2);

            String response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String diffContent = extractContent(response);

            Map<String, Object> structured = mapper.readValue(diffContent, Map.class);

            ResumeComparisonDiff diff = new ResumeComparisonDiff();
            diff.setOldResume(oldResume);
            diff.setNewResume(newResume);
            diff.setDifferences((Map) structured);

            return diff;

        } catch (Exception e) {
            ResumeComparisonDiff diff = new ResumeComparisonDiff();
            diff.setOldResume(oldResume);
            diff.setNewResume(newResume);
            diff.setDifferences(Map.of("error", "Groq API Error: " + e.getMessage()));
            return diff;
        }
    }

    
    
    public String generateResumeFromPdf(String normalizedText) {
        try {
            String aiPrompt = """
                You are a resume-parsing AI.

                Given the resume text below, extract the resume into JSON with the following exact structure:

                {
                  "resumeDetails": {
                    "name": "",
                    "title": "",
                    "contact": {
                      "phone": "",
                      "email": "",
                      "linkedin": "",
                      "github": "",
                      "location": ""
                    },
                    "summary": ""
                  },
                  "skills": [
                    "Programming Languages - ...",
                    "Databases - ...",
                    "Frameworks & Libraries - ...",
                    "Tools & Platforms - ...",
                    "Cloud & Deployment - ...",
                    "Soft Skills - ..."
                  ],
                  "experiences": [
                    {
                      "title": "",
                      "company": "",
                      "location": "",
                      "startDate": "",
                      "endDate": "",
                      "description": [""]
                    }
                  ],
                  "projects": [
                    {
                      "name": "",
                      "description": [""]
                    }
                  ],
                  "educationList": [
                    {
                      "degree": "",
                      "cgpa": "",
                      "university": "",
                      "startDate": "",
                      "endDate": ""
                    }
                  ],
                  "certifications": [
                    {
                      "title": "",
                      "issuer": "",
                      "date": ""
                    }
                  ]
                }

                - If any section is missing in the resume text, generate a reasonable default or leave blank.
                - Group skills into these six categories exactly as:
                  Programming Languages, Databases, Frameworks & Libraries, Tools & Platforms, Cloud & Deployment, Soft Skills.
                - Return ONLY valid JSON. Do NOT include markdown, commentary, or any text outside JSON.

                Resume Text:
                %s
                """.formatted(normalizedText);

            JSONObject body = new JSONObject();
            body.put("model", model);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You must return only JSON. Never return text outside JSON."));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", aiPrompt));

            body.put("messages", messages);
            body.put("temperature", 0.2);
            body.put("max_tokens", 3000);

            Mono<String> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body.toString())
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();
            System.out.println(response);
            return response; // raw JSON string

        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"AI Resume generation failed: " + e.getMessage().replace("\"", "'") + "\" }";
        }
    }



}
