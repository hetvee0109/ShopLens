package com.shoplens.shoplensbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoplens.shoplensbackend.model.AnalysisResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
// @Service tells Spring: "This class contains business logic, manage it for me"
public class ShopService {

    // ── Method 1: The existing getResults() from Day 2 — keep it ──
    public List<AnalysisResult> getResults() {
        // Return empty list for now — will be replaced by real data later
        return new ArrayList<>();
    }

    // ── Method 2: NEW — Analyze uploaded CSV file ──
    public Map<String, Object> analyzeFile(MultipartFile file) throws Exception {
        // MultipartFile = the CSV file uploaded by the user through the API

        // ── Step A: Save the uploaded file to a temporary location on disk ──
        // We can't pass a MultipartFile to Python — Python needs an actual file path
        // So we save it to the system's temp folder first

        String originalFilename = file.getOriginalFilename();
        // getOriginalFilename() = the name the user gave their file, e.g. "sales_data.csv"
        Path tempFile = Files.createTempFile("upload_", "_" + originalFilename);
        // Files.createTempFile() creates an empty file in the OS temp folder
        // "upload_" is a prefix, "_" + originalFilename is the suffix

        // Copy the uploaded file content into the temp file
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        // file.getInputStream() = reads the bytes of the uploaded file
        // StandardCopyOption.REPLACE_EXISTING = if temp file already exists, overwrite it

        String csvFilePath = tempFile.toAbsolutePath().toString();
        // toAbsolutePath() = gives us the full path like C:\Users\admin\AppData\...
        // toString() = converts the Path object to a plain String

        // ── Step B: Build the command to run Python ──
        // This is equivalent to typing in terminal:
        // python C:\Users\admin\Desktop\ShopLens\apriori_runner.py <csvFilePath>

        String pythonScriptPath = "C:\\Users\\admin\\Desktop\\ShopLens\\apriori_runner.py";
        // This is the absolute path to our Python script
        // We use \\ because in Java strings, \ is an escape character — \\ means one \

        List<String> command = new ArrayList<>();
        command.add("python");          // The command (run Python)
        command.add(pythonScriptPath);  // First argument: path to our script
        command.add(csvFilePath);       // Second argument: path to the CSV file

        // ── Step C: Use ProcessBuilder to run the command ──
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // ProcessBuilder = Java's way to run external programs (like opening a terminal)

        processBuilder.redirectErrorStream(true);
        // redirectErrorStream(true) = merge error output with normal output
        // So if Python prints an error, Java will see it too

        Process process = processBuilder.start();
        // process.start() = actually runs the command — Python starts executing now

        // ── Step D: Read Python's console output ──
        // Python will print JSON to the console — we read it here

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        // process.getInputStream() = connects to Python's console output (stdout)
        // InputStreamReader = converts raw bytes to characters
        // BufferedReader = reads line by line efficiently

        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            // readLine() reads one line at a time
            // When Python finishes and closes, readLine() returns null — loop ends
            outputBuilder.append(line);
        }

        int exitCode = process.waitFor();
        // waitFor() = Java WAITS here until Python completely finishes
        // Returns 0 if Python exited normally, non-zero if there was an error

        String pythonOutput = outputBuilder.toString();
        // This is the full JSON string that Python printed

        // ── Step E: Delete the temp file (cleanup) ──
        Files.deleteIfExists(tempFile);
        // We don't need the temp file anymore — clean up disk space

        // ── Step F: Check if Python ran successfully ──
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code: " + exitCode
                    + ". Output: " + pythonOutput);
        }

        if (pythonOutput.isEmpty()) {
            throw new RuntimeException("Python script returned no output");
        }

        // ── Step G: Parse the JSON that Python returned ──
        ObjectMapper objectMapper = new ObjectMapper();
        // ObjectMapper = Jackson library's tool for reading/writing JSON
        // Spring Boot includes Jackson automatically

        JsonNode jsonNode = objectMapper.readTree(pythonOutput);
        // readTree() = parses JSON string into a tree structure we can navigate

        // ── Step H: Build the response map ──
        Map<String, Object> response = new HashMap<>();

        if (jsonNode.has("error")) {
            // If Python returned {"error": "something went wrong"}
            response.put("success", false);
            response.put("error", jsonNode.get("error").asText());
        } else {
            // If Python returned {"success": true, "rules": [...]}
            response.put("success", true);
            response.put("rules", jsonNode.get("rules"));
            // We pass the rules JsonNode directly — Spring will serialize it to JSON for us
        }

        return response;
        // This Map will be automatically converted to JSON by Spring Boot
    }
}