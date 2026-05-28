package com.shoplens.shoplensbackend.controller;

import com.shoplens.shoplensbackend.model.AnalysisResult;
import com.shoplens.shoplensbackend.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
// @RestController = This class handles HTTP requests and returns JSON automatically

@RequestMapping("/api")
// @RequestMapping("/api") = All endpoints in this class start with /api

@CrossOrigin(origins = "*")
// @CrossOrigin = Allow requests from ANY domain (needed for React frontend later)

public class ShopController {

    @Autowired
    // @Autowired = Spring automatically creates and injects the ShopService object here
    private ShopService shopService;

    // ── Endpoint 1: Health check (from Day 2) ──
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ShopLens backend is running!");
    }

    // ── Endpoint 2: Get results (from Day 2) ──
    @GetMapping("/results")
    public ResponseEntity<List<AnalysisResult>> getResults() {
        return ResponseEntity.ok(shopService.getResults());
    }

    // ── Endpoint 3: NEW — Upload CSV and analyze ──
    @PostMapping("/upload")
    // @PostMapping = This handles HTTP POST requests to /api/upload

    public ResponseEntity<Map<String, Object>> uploadAndAnalyze(
            @RequestParam("file") MultipartFile file) {
        // @RequestParam("file") = Look for a form field named "file" in the request
        // MultipartFile file = The actual uploaded file

        // ── Validation: Check if file is empty ──
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Please upload a CSV file"));
            // Map.of() = quick way to create a Map with key-value pairs
            // ResponseEntity.badRequest() = returns HTTP 400 status
        }

        // ── Validation: Check file extension ──
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Only CSV files are allowed"));
        }

        // ── Call the service to process the file ──
        try {
            Map<String, Object> result = shopService.analyzeFile(file);
            // analyzeFile() does all the heavy lifting — saving file, calling Python, parsing output

            return ResponseEntity.ok(result);
            // ResponseEntity.ok() = returns HTTP 200 with the result as JSON

        } catch (Exception e) {
            // If anything goes wrong, return a 500 error with the message
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}