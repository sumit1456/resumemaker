package com.app.resumemaker.controller;

import com.app.resumemaker.service.PdfExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class PdfExtractorController {

    @Autowired
    private PdfExtractionService extractionService;

    @PostMapping("/pdf-extraction-config")
    public ResponseEntity<Map<String, Object>> extractPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Service logic to process the bytes
            Map<String, Object> result = extractionService.extract(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
