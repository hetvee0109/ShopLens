package com.shoplens.shoplensbackend.controller;

// Spring annotations
import org.springframework.web.bind.annotation.CrossOrigin;  // Allow React to call this API
import org.springframework.web.bind.annotation.GetMapping;   // Handle GET requests
import org.springframework.web.bind.annotation.RequestMapping; // Base URL for this controller
import org.springframework.web.bind.annotation.RestController; // This is a REST API controller

// Our service
import com.shoplens.shoplensbackend.service.ShopService;

// Our model
import com.shoplens.shoplensbackend.model.AnalysisResult;

// Spring's dependency injection
import org.springframework.beans.factory.annotation.Autowired;

// Java list
import java.util.List;

// This annotation means:
// "This class handles HTTP requests AND automatically converts return values to JSON"
@RestController

// All URLs in this class will start with /api
// So /health becomes /api/health
// And /results becomes /api/results
@RequestMapping("/api")

// CORS = Cross-Origin Resource Sharing
// React runs on localhost:3000, Spring Boot runs on localhost:8080
// By default browsers BLOCK requests between different ports (security feature)
// @CrossOrigin says: "Allow requests from localhost:3000 (our React app)"
@CrossOrigin(origins = "http://localhost:3000")

public class ShopController {

    // @Autowired tells Spring: "Find the ShopService object you already created
    // and inject (insert) it here automatically"
    // We don't write: ShopService service = new ShopService(); manually
    // Spring handles object creation and sharing — this is called Dependency Injection
    @Autowired
    private ShopService shopService;

    // This method handles: GET http://localhost:8080/api/health
    // It returns a plain text string
    @GetMapping("/health")
    public String healthCheck() {
        // Call our service method and return the result
        return shopService.getHealthStatus();
    }

    // This method handles: GET http://localhost:8080/api/results
    // It returns a List<AnalysisResult> which Spring AUTOMATICALLY converts to JSON array
    @GetMapping("/results")
    public List<AnalysisResult> getResults() {
        // Call our service method which returns sample Apriori results
        return shopService.getSampleResults();
    }



    // This method handles: GET http://localhost:8080/api/info
    // Returns basic info about the application as a simple string
    @GetMapping("/info")
    public String getInfo() {
        return "ShopLens v1.0 | Market Basket Analysis Platform | Built with Spring Boot + Python";
    }
}
