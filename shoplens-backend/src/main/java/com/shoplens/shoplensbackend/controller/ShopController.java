package com.shoplens.shoplensbackend.controller;

import com.shoplens.shoplensbackend.model.AnalysisResult;
import com.shoplens.shoplensbackend.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
// @RestController = every method returns JSON data, not HTML
// Combines @Controller + @ResponseBody

@RequestMapping("/api")
// All routes in this class start with /api
// /upload → /api/upload
// /history → /api/history

@CrossOrigin(origins = "*")
// Allows React frontend (port 3000) to call this backend (port 8080)
// Without this, browser blocks the request due to CORS security policy

public class ShopController {

    @Autowired
    // Spring automatically creates and injects ShopService here
    // You never write: ShopService shopService = new ShopService();
    // Spring does that for you — this is called Dependency Injection
    private ShopService shopService;

    // ── POST /api/upload ────────────────────────────────────────
    @PostMapping("/upload")
    // Handles HTTP POST requests sent to http://localhost:8080/api/upload
    public ResponseEntity<List<AnalysisResult>> uploadFile(
            @RequestPart("file") MultipartFile file
            // @RequestPart("file") = grab the file from the multipart form body
            // "file" must match the Key name you set in Postman form-data
    ) {
        try {
            List<AnalysisResult> results = shopService.runAnalysis(file);
            // 1. Saves CSV to disk temporarily
            // 2. Runs Python apriori_runner.py
            // 3. Parses JSON output from Python
            // 4. Saves each rule to MySQL
            // 5. Returns the saved list

            return ResponseEntity.ok(results);
            // HTTP 200 + results as JSON array in response body

        } catch (Exception e) {
            // Print the full error stack trace in IntelliJ console
            // This helps you see exactly which line caused the crash
            e.printStackTrace();

            // Also send the error message back to Postman so you can read it
            // This is much better than an empty 500 response
            System.out.println("UPLOAD ERROR: " + e.getMessage());

            return ResponseEntity.internalServerError().build();
            // HTTP 500 — something crashed on the server side
        }
    }

    // ── GET /api/history ───────────────────────────────────────
    @GetMapping("/history")
    // Handles HTTP GET requests sent to http://localhost:8080/api/history
    // No request body needed — just call the URL and get all past results
    public ResponseEntity<List<AnalysisResult>> getHistory() {

        try {
            List<AnalysisResult> results = shopService.getAllResults();
            // Calls repository.findAll()
            // Hibernate generates: SELECT * FROM analysis_results
            // Returns every saved row as a List of Java objects
            // Spring converts that List to a JSON array automatically

            return ResponseEntity.ok(results);
            // HTTP 200 + all rows as JSON array

        } catch (Exception e) {
            e.printStackTrace();
            // Print error in IntelliJ console if DB query fails

            return ResponseEntity.internalServerError().build();
            // HTTP 500 if something goes wrong reading from MySQL
        }
    }
}