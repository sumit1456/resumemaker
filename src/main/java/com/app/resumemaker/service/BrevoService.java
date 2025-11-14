package com.app.resumemaker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.util.Value;

@Service
public class BrevoService {

    @Value("${brevo.api.key}")
    private String apiKey; 

    private final WebClient webClient = WebClient.create("https://api.brevo.com/v3");

    public void sendVerificationEmail(String toEmail, String token) {
        System.out.println("📨 BrevoService.sendVerificationEmail() called for: " + toEmail);

        String verifyLink = "http://localhost:5173/verify?token=" + token;

        // 🧩 Styled HTML email template
        String htmlContent = """
            <html>
              <body style="font-family: Arial, sans-serif; background-color: #f8f9fa; margin: 0; padding: 0;">
                <table width="100%%" cellspacing="0" cellpadding="0" style="max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                  <tr>
                    <td style="padding: 40px; text-align: center;">
                      <h1 style="color: #1a73e8; font-size: 28px; margin-bottom: 20px;">Verify Your Email</h1>
                      <p style="font-size: 18px; color: #333;">Hi there 👋,</p>
                      <p style="font-size: 16px; color: #555;">
                        Thank you for signing up with <strong>Resume Maker</strong>!<br>
                        Please verify your email address to activate your account.
                      </p>
                      <p style="margin: 30px 0;">
                        <a href="%s" 
                           style="background-color: #1a73e8; color: #fff; padding: 14px 28px; border-radius: 6px; 
                                  text-decoration: none; font-size: 18px; font-weight: bold;">
                           Verify My Email
                        </a>
                      </p>
                      <p style="font-size: 14px; color: #777;">
                        This link will expire in 24 hours.<br>
                        If you didn’t create this account, you can safely ignore this message.
                      </p>
                      <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                      <p style="font-size: 13px; color: #aaa;">
                        &copy; 2025 Resume Maker. All rights reserved.
                      </p>
                    </td>
                  </tr>
                </table>
              </body>
            </html>
            """.formatted(verifyLink);

        // 🧠 Construct the email payload clearly
        Map<String, Object> emailData = Map.of(
            "sender", Map.of(
                "email", "sumithatekar9@gmail.com",  // must be verified sender in Brevo
                "name", "Resume Maker"
            ),
            "to", List.of(Map.of("email", toEmail)),
            "subject", "Verify your Resume Maker account",
            "htmlContent", htmlContent
        );

        System.out.println("📦 Sending payload to Brevo: " + emailData);

        webClient.post()
            .uri("/smtp/email")
            .header("accept", "application/json")
            .header("api-key", apiKey)
            .bodyValue(emailData)
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> System.out.println("✅ Brevo Response: " + response))
            .doOnError(err -> System.err.println("❌ Brevo Error: " + err.getMessage()))
            .subscribe();
    }
    
    
    
    
    
    
    
    
}
