package com.shoplens.shoplensbackend.service;

import com.shoplens.shoplensbackend.model.*;
import com.shoplens.shoplensbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// This service is ONLY for admin operations
// Every method here should only be callable if the user has role = ADMIN
// For Day 6, we check this manually in the controller
// Day 8 will enforce this automatically via JWT
public class AdminService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DiscountSuggestionRepository discountSuggestionRepository;

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    private CsvUploadRepository csvUploadRepository;

    // -----------------------------------------------------------------------
    // Owner management
    // -----------------------------------------------------------------------

    // Return every owner account in the system
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
        // findAll() is inherited from JpaRepository
        // SELECT * FROM owners
    }

    // Disable an owner's account without deleting their data
    public Owner deactivateOwner(long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        owner.setIsActive(false);
        return ownerRepository.save(owner);
        // save() on existing entity = UPDATE SET is_active = false
    }

    // Permanently delete an owner and ALL their data
    // Cascade: because foreign keys reference owner_id, delete the owner's data first
    public void deleteOwner(long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        // Delete all related data first (foreign key constraint)
        // If you delete the owner row first, MySQL will throw a FK violation error
        List<AnalysisResult> results = analysisResultRepository.findByOwnerId(ownerId);
        analysisResultRepository.deleteAll(results);

        List<Inventory> items = inventoryRepository.findByOwnerId(ownerId);
        inventoryRepository.deleteAll(items);

        List<DiscountSuggestion> discounts = discountSuggestionRepository.findByOwnerId(ownerId);
        discountSuggestionRepository.deleteAll(discounts);

        List<MonthlySummary> summaries = monthlySummaryRepository.findByOwnerId(ownerId);
        monthlySummaryRepository.deleteAll(summaries);

        List<CsvUpload> uploads = csvUploadRepository.findByOwnerIdOrderByUploadedAtDesc(ownerId);
        csvUploadRepository.deleteAll(uploads);

        // Now it's safe to delete the owner row
        ownerRepository.delete(owner);
    }

    // -----------------------------------------------------------------------
    // Data visibility — admin sees ALL rows from ALL owners
    // -----------------------------------------------------------------------

    public List<AnalysisResult> getAllAnalysisResults() {
        return analysisResultRepository.findAll();
        // No WHERE clause — returns every row from every owner
    }

    public List<AnalysisResult> getResultsByOwner(long ownerId) {
        return analysisResultRepository.findByOwnerId(ownerId);
        // Admin drilling into one specific owner's results
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<DiscountSuggestion> getAllDiscountSuggestions() {
        return discountSuggestionRepository.findAll();
    }

    public List<MonthlySummary> getAllMonthlySummaries() {
        return monthlySummaryRepository.findAll();
    }

    public List<CsvUpload> getAllCsvUploads() {
        return csvUploadRepository.findAll();
        // Full audit log — every upload from every owner
    }
}