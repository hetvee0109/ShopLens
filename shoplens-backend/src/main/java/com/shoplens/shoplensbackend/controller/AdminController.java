package com.shoplens.shoplensbackend.controller;

import com.shoplens.shoplensbackend.model.*;
import com.shoplens.shoplensbackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// All admin endpoints live under /api/admin/
// In Day 8, Spring Security will block any non-ADMIN from reaching this path

@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ------------------------------------------------------------------
    // GET /api/admin/owners
    // Returns every owner account in the system
    // ------------------------------------------------------------------
    @GetMapping("/owners")
    public ResponseEntity<List<Owner>> getAllOwners() {
        return ResponseEntity.ok(adminService.getAllOwners());
    }

    // ------------------------------------------------------------------
    // PUT /api/admin/owners/{ownerId}/deactivate
    // Disables an owner's login without deleting their data
    // PUT = "update this resource"
    // ------------------------------------------------------------------
    @PutMapping("/owners/{ownerId}/deactivate")
    public ResponseEntity<Owner> deactivateOwner(@PathVariable long ownerId) {
        Owner updated = adminService.deactivateOwner(ownerId);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------------
    // DELETE /api/admin/owners/{ownerId}
    // Permanently deletes the owner and all their data
    // ------------------------------------------------------------------
    @DeleteMapping("/owners/{ownerId}")
    public ResponseEntity<String> deleteOwner(@PathVariable long ownerId) {
        adminService.deleteOwner(ownerId);
        return ResponseEntity.ok("Owner " + ownerId + " and all their data have been deleted.");
    }

    // ------------------------------------------------------------------
    // GET /api/admin/results
    // Returns ALL analysis rules from ALL owners
    // ------------------------------------------------------------------
    @GetMapping("/results")
    public ResponseEntity<List<AnalysisResult>> getAllResults() {
        return ResponseEntity.ok(adminService.getAllAnalysisResults());
    }

    // ------------------------------------------------------------------
    // GET /api/admin/results/{ownerId}
    // Returns analysis rules for ONE specific owner (admin drill-down)
    // ------------------------------------------------------------------
    @GetMapping("/results/{ownerId}")
    public ResponseEntity<List<AnalysisResult>> getResultsByOwner(
            @PathVariable long ownerId) {
        return ResponseEntity.ok(adminService.getResultsByOwner(ownerId));
    }

    // ------------------------------------------------------------------
    // GET /api/admin/inventory
    // Returns ALL inventory items from ALL owners
    // ------------------------------------------------------------------
    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(adminService.getAllInventory());
    }

    // ------------------------------------------------------------------
    // GET /api/admin/discounts
    // Returns ALL discount suggestions from ALL owners
    // ------------------------------------------------------------------
    @GetMapping("/discounts")
    public ResponseEntity<List<DiscountSuggestion>> getAllDiscounts() {
        return ResponseEntity.ok(adminService.getAllDiscountSuggestions());
    }

    // ------------------------------------------------------------------
    // GET /api/admin/summaries
    // Returns ALL monthly summaries from ALL owners
    // ------------------------------------------------------------------
    @GetMapping("/summaries")
    public ResponseEntity<List<MonthlySummary>> getAllSummaries() {
        return ResponseEntity.ok(adminService.getAllMonthlySummaries());
    }

    // ------------------------------------------------------------------
    // GET /api/admin/uploads
    // Full CSV upload audit log — every upload from every owner
    // ------------------------------------------------------------------
    @GetMapping("/uploads")
    public ResponseEntity<List<CsvUpload>> getAllUploads() {
        return ResponseEntity.ok(adminService.getAllCsvUploads());
    }
}