package com.shoplens.shoplensbackend.service;

import com.shoplens.shoplensbackend.model.*;
import com.shoplens.shoplensbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Service
// @Service tells Spring: "this class is a service bean — manage it for us"
public class ShopService {

    // Spring auto-injects all these repositories
    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DiscountSuggestionRepository discountSuggestionRepository;

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private CsvUploadRepository csvUploadRepository;

    // -----------------------------------------------------------------------
    // METHOD: analyzeFile
    // Called when an owner uploads a CSV
    // Steps: save upload log → run Python → parse results → save to DB
    // -----------------------------------------------------------------------
    public List<AnalysisResult> analyzeFile(MultipartFile file,
                                            long ownerId,
                                            int month,
                                            int year) throws Exception {

        // Step 1: Find the owner in DB
        // orElseThrow: if no owner with this ID, throw an exception
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        // Step 2: Create an upload log row (status = PROCESSING)
        CsvUpload uploadLog = new CsvUpload();
        uploadLog.setOwner(owner);
        uploadLog.setFileName(file.getOriginalFilename());
        uploadLog.setMonth(month);
        uploadLog.setYear(year);
        uploadLog.setStatus(UploadStatus.PROCESSING);
        uploadLog.setUploadedAt(LocalDateTime.now());
        csvUploadRepository.save(uploadLog);
        // This inserts a row — now the admin can see someone is uploading

        try {
            // Step 3: Save the uploaded file to disk so Python can read it
            File tempFile = File.createTempFile("upload_", ".csv");
            // createTempFile creates a temporary file in the OS temp folder
            file.transferTo(tempFile);
            // transferTo copies the uploaded bytes into that temp file

            // Step 4: Run the Python script
            ProcessBuilder pb = new ProcessBuilder(
                    "python", "src/main/resources/apriori_script.py",
                    tempFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Step 5: Read Python's output line by line
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            List<AnalysisResult> results = new ArrayList<>();
            String line;
            int rowCount = 0;

            while ((line = reader.readLine()) != null) {
                // Python outputs lines like:
                // antecedents,consequents,support,confidence,lift
                // Bread,Butter,0.4,0.8,2.1

                if (line.startsWith("antecedents")) continue;
                // Skip the header row

                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                // Skip malformed lines

                // Step 6: Build an AnalysisResult object
                AnalysisResult result = new AnalysisResult();
                result.setOwner(owner);          // link to this owner
                result.setAntecedents(parts[0].trim());
                result.setConsequents(parts[1].trim());
                result.setSupport(Double.parseDouble(parts[2].trim()));
                result.setConfidence(Double.parseDouble(parts[3].trim()));
                result.setLift(Double.parseDouble(parts[4].trim()));
                result.setMonth(month);
                result.setYear(year);

                results.add(result);
                rowCount++;
            }

            process.waitFor();
            // Wait for Python to finish before continuing

            // Step 7: Save all analysis results to the database
            analysisResultRepository.saveAll(results);

            // Step 8: Generate discount suggestions based on the rules
            generateDiscountSuggestions(owner, results, month, year);

            // Step 9: Save the monthly summary
            saveMonthlySummary(owner, results, month, year);

            // Step 10: Update the upload log to SUCCESS
            uploadLog.setRowCount(rowCount);
            uploadLog.setStatus(UploadStatus.SUCCESS);
            csvUploadRepository.save(uploadLog);
            // save() on an existing entity = UPDATE (not INSERT)

            // Clean up temp file
            tempFile.delete();

            return results;

        } catch (Exception e) {
            // If anything goes wrong, mark the upload as FAILED
            uploadLog.setStatus(UploadStatus.FAILED);
            uploadLog.setErrorMessage(e.getMessage());
            csvUploadRepository.save(uploadLog);
            throw e;
            // Re-throw so the controller can return a 500 error to the client
        }
    }

    // -----------------------------------------------------------------------
    // METHOD: generateDiscountSuggestions
    // Private helper — called inside analyzeFile
    // Logic: if lift > 2.0, suggest a bundle discount
    // -----------------------------------------------------------------------
    private void generateDiscountSuggestions(Owner owner,
                                             List<AnalysisResult> results,
                                             int month,
                                             int year) {

        // Delete old suggestions for this owner/month (avoid duplicates on re-upload)
        List<DiscountSuggestion> old =
                discountSuggestionRepository.findByOwnerIdAndMonthAndYear(
                        owner.getId(), month, year);
        discountSuggestionRepository.deleteAll(old);

        List<DiscountSuggestion> suggestions = new ArrayList<>();

        for (AnalysisResult result : results) {
            if (result.getLift() > 2.0) {
                // High lift = strong association = good candidate for a bundle discount

                DiscountSuggestion suggestion = new DiscountSuggestion();
                suggestion.setOwner(owner);
                suggestion.setItemset(result.getAntecedents() + " + " + result.getConsequents());
                suggestion.setSuggestedDiscountPct(calculateDiscount(result.getLift()));
                suggestion.setReason("High lift (" + result.getLift() + ") — customers often buy these together");
                suggestion.setBasedOnLift(result.getLift());
                suggestion.setBasedOnConfidence(result.getConfidence());
                suggestion.setMonth(month);
                suggestion.setYear(year);
                suggestion.setCreatedAt(LocalDateTime.now());

                suggestions.add(suggestion);
            }
        }

        discountSuggestionRepository.saveAll(suggestions);
    }

    // -----------------------------------------------------------------------
    // Helper: calculate discount % based on lift value
    // lift 2–3 → 10%, lift 3–4 → 15%, lift 4+ → 20%
    // -----------------------------------------------------------------------
    private int calculateDiscount(double lift) {
        if (lift >= 4.0) return 20;
        if (lift >= 3.0) return 15;
        return 10;
    }

    // -----------------------------------------------------------------------
    // METHOD: saveMonthlySummary
    // Aggregates stats from the analysis results and saves one summary row
    // -----------------------------------------------------------------------
    private void saveMonthlySummary(Owner owner,
                                    List<AnalysisResult> results,
                                    int month,
                                    int year) {

        // Check if a summary already exists for this owner/month (re-upload case)
        MonthlySummary summary = monthlySummaryRepository
                .findByOwnerIdAndMonthAndYear(owner.getId(), month, year)
                .orElse(new MonthlySummary());
        // orElse(new MonthlySummary()) = if not found, create a new one

        summary.setOwner(owner);
        summary.setMonth(month);
        summary.setYear(year);
        summary.setTotalRulesFound(results.size());
        summary.setUploadedAt(LocalDateTime.now());

        // Calculate average lift across all rules
        OptionalDouble avgLift = results.stream()
                .mapToDouble(AnalysisResult::getLift)
                .average();
        summary.setAvgLift(avgLift.orElse(0.0));

        // Calculate average confidence
        OptionalDouble avgConf = results.stream()
                .mapToDouble(AnalysisResult::getConfidence)
                .average();
        summary.setAvgConfidence(avgConf.orElse(0.0));

        // Find the top itemset (highest lift rule)
        results.stream()
                .max((a, b) -> Double.compare(a.getLift(), b.getLift()))
                .ifPresent(top -> summary.setTopItemset(
                        top.getAntecedents() + " → " + top.getConsequents()));

        monthlySummaryRepository.save(summary);
    }

    // -----------------------------------------------------------------------
    // OWNER QUERIES — these enforce data isolation
    // Each method takes ownerId and only returns that owner's data
    // -----------------------------------------------------------------------

    public List<AnalysisResult> getHistoryByOwner(long ownerId) {
        return analysisResultRepository.findByOwnerId(ownerId);
    }

    public List<AnalysisResult> getHistoryByOwnerAndMonth(long ownerId, int month, int year) {
        return analysisResultRepository.findByOwnerIdAndMonthAndYear(ownerId, month, year);
    }

    public List<Inventory> getInventory(long ownerId) {
        return inventoryRepository.findByOwnerId(ownerId);
    }

    public Inventory saveOrUpdateInventory(long ownerId, Inventory item) {
        // Find the owner
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        // Check if this item already exists for this owner
        Inventory existing = inventoryRepository
                .findByOwnerIdAndItemName(ownerId, item.getItemName())
                .orElse(null);

        if (existing != null) {
            // Update the existing row instead of inserting a duplicate
            existing.setStockQuantity(item.getStockQuantity());
            existing.setReorderLevel(item.getReorderLevel());
            existing.setUnitPrice(item.getUnitPrice());
            existing.setCategory(item.getCategory());
            return inventoryRepository.save(existing);
        } else {
            // New item — set the owner and insert
            item.setOwner(owner);
            return inventoryRepository.save(item);
        }
    }

    public List<DiscountSuggestion> getDiscountSuggestions(long ownerId, int month, int year) {
        return discountSuggestionRepository.findByOwnerIdAndMonthAndYear(ownerId, month, year);
    }

    public List<MonthlySummary> getMonthlySummaries(long ownerId) {
        return monthlySummaryRepository.findByOwnerId(ownerId);
    }
}