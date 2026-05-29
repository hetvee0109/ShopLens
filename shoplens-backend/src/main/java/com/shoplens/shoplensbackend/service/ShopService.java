package com.shoplens.shoplensbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoplens.shoplensbackend.model.AnalysisResult;
import com.shoplens.shoplensbackend.repository.AnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    @Autowired
    private AnalysisResultRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<AnalysisResult> runAnalysis(MultipartFile file) throws IOException, InterruptedException {

        // ── Save uploaded CSV to disk temporarily ──────────────
        File tempFile = File.createTempFile("upload_", ".csv");
        file.transferTo(tempFile);

        // ── Run Python script ──────────────────────────────────
        ProcessBuilder pb = new ProcessBuilder(
                "python",
                "C:\\Users\\admin\\Desktop\\ShopLens\\apriori_runner.py",
                tempFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Python script failed: " + output);
        }

        // ── Parse the wrapper object first ─────────────────────
        // Python returns: {"success": true, "rules": [...]}
        // We must read the TOP LEVEL object first, then get "rules" key
        JsonNode root = objectMapper.readTree(output);
        // root = the whole {"success":true, "rules":[...]} object

        // Check if Python reported success
        boolean success = root.get("success").asBoolean();
        if (!success) {
            throw new RuntimeException("Python reported failure: " + output);
        }

        // Get the "rules" array from inside the wrapper object
        JsonNode rulesArray = root.get("rules");
        // rulesArray = the [...] part only, which is what we actually loop through

        // ── Convert each rule + save to MySQL ──────────────────
        List<AnalysisResult> savedResults = new ArrayList<>();

        for (JsonNode node : rulesArray) {
            // Each node looks like:
            // {
            //   "antecedents": ["oil", "soap", "tea"],   ← this is a JSON ARRAY
            //   "consequents": ["salt", "rice"],          ← this is a JSON ARRAY
            //   "support": 0.01,
            //   "confidence": 1.0,
            //   "lift": 3.4091
            // }

            AnalysisResult result = new AnalysisResult();

            // ── Handle antecedents array → join into one string ──
            // ["oil", "soap", "tea"] → "oil, soap, tea"
            JsonNode antecedentsNode = node.get("antecedents");
            // antecedentsNode is a JSON array like ["oil","soap","tea"]

            List<String> antecedentsList = new ArrayList<>();
            for (JsonNode item : antecedentsNode) {
                antecedentsList.add(item.asText());
                // item.asText() converts each "oil", "soap" etc to Java String
            }
            result.setAntecedents(String.join(", ", antecedentsList));
            // String.join(", ", list) = "oil, soap, tea"
            // Now it's one readable string we can store in MySQL

            // ── Handle consequents array → join into one string ──
            // ["salt", "rice"] → "salt, rice"
            JsonNode consequentsNode = node.get("consequents");
            List<String> consequentsList = new ArrayList<>();
            for (JsonNode item : consequentsNode) {
                consequentsList.add(item.asText());
            }
            result.setConsequents(String.join(", ", consequentsList));

            // ── Numeric fields (these are plain numbers, no change) ──
            result.setSupport(node.get("support").asDouble());
            result.setConfidence(node.get("confidence").asDouble());
            result.setLift(node.get("lift").asDouble());

            // ── Save this rule to MySQL ───────────────────────────
            AnalysisResult saved = repository.save(result);
            // Hibernate generates INSERT INTO analysis_results (...) VALUES (...)
            // MySQL auto-assigns the id

            savedResults.add(saved);
        }

        // ── Delete temp file ───────────────────────────────────
        tempFile.delete();

        return savedResults;
    }

    public List<AnalysisResult> getAllResults() {
        return repository.findAll();
    }
}