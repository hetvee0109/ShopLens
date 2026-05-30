package com.shoplens.shoplensbackend.controller;

import com.shoplens.shoplensbackend.model.*;
import com.shoplens.shoplensbackend.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
// @RestController = @Controller + @ResponseBody
// Every method returns JSON automatically

@RequestMapping("/api")
// All endpoints in this controller start with /api

@CrossOrigin(origins = "http://localhost:3000")
// Allow React dev server to call this API
public class ShopController {

    @Autowired
    private ShopService shopService;

    // ------------------------------------------------------------------
    // POST /api/upload
    // Owner uploads a CSV for analysis
    // Params: file (the CSV), ownerId, month, year
    // ------------------------------------------------------------------
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") long ownerId,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        // Basic validation — did they actually send a file?
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            List<AnalysisResult> results =
                    shopService.analyzeFile(file, ownerId, month, year);
            return ResponseEntity.ok(results);
            // 200 OK with JSON array of rules

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Analysis failed: " + e.getMessage());
            // 500 Internal Server Error
        }
    }

    // ------------------------------------------------------------------
    // GET /api/history/{ownerId}
    // Get ALL analysis results for one owner across all months
    // ------------------------------------------------------------------
    @GetMapping("/history/{ownerId}")
    public ResponseEntity<List<AnalysisResult>> getHistory(
            @PathVariable long ownerId) {
        // @PathVariable extracts {ownerId} from the URL

        List<AnalysisResult> history = shopService.getHistoryByOwner(ownerId);
        return ResponseEntity.ok(history);
    }

    // ------------------------------------------------------------------
    // GET /api/history/{ownerId}/{month}/{year}
    // Get results for one owner for a specific month
    // ------------------------------------------------------------------
    @GetMapping("/history/{ownerId}/{month}/{year}")
    public ResponseEntity<List<AnalysisResult>> getHistoryByMonth(
            @PathVariable long ownerId,
            @PathVariable int month,
            @PathVariable int year) {

        List<AnalysisResult> history =
                shopService.getHistoryByOwnerAndMonth(ownerId, month, year);
        return ResponseEntity.ok(history);
    }

    // ------------------------------------------------------------------
    // GET /api/inventory/{ownerId}
    // Get all inventory items for one owner
    // ------------------------------------------------------------------
    @GetMapping("/inventory/{ownerId}")
    public ResponseEntity<List<Inventory>> getInventory(
            @PathVariable long ownerId) {

        List<Inventory> inventory = shopService.getInventory(ownerId);
        return ResponseEntity.ok(inventory);
    }

    // ------------------------------------------------------------------
    // POST /api/inventory/{ownerId}
    // Add or update an inventory item
    // ------------------------------------------------------------------
    @PostMapping("/inventory/{ownerId}")
    public ResponseEntity<Inventory> saveInventory(
            @PathVariable long ownerId,
            @RequestBody Inventory item) {
        // @RequestBody parses the JSON request body into an Inventory object

        Inventory saved = shopService.saveOrUpdateInventory(ownerId, item);
        return ResponseEntity.ok(saved);
    }

    // ------------------------------------------------------------------
    // GET /api/discounts/{ownerId}/{month}/{year}
    // Get discount suggestions for one owner for a specific month
    // ------------------------------------------------------------------
    @GetMapping("/discounts/{ownerId}/{month}/{year}")
    public ResponseEntity<List<DiscountSuggestion>> getDiscounts(
            @PathVariable long ownerId,
            @PathVariable int month,
            @PathVariable int year) {

        List<DiscountSuggestion> discounts =
                shopService.getDiscountSuggestions(ownerId, month, year);
        return ResponseEntity.ok(discounts);
    }

    // ------------------------------------------------------------------
    // GET /api/summary/{ownerId}
    // Get all monthly summaries for one owner
    // ------------------------------------------------------------------
    @GetMapping("/summary/{ownerId}")
    public ResponseEntity<List<MonthlySummary>> getSummaries(
            @PathVariable long ownerId) {

        List<MonthlySummary> summaries = shopService.getMonthlySummaries(ownerId);
        return ResponseEntity.ok(summaries);
    }
}