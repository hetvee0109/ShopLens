package com.shoplens.shoplensbackend.service;

// @Service tells Spring: "This class contains business logic, manage it for me"
import org.springframework.stereotype.Service;

// We need our model class
import com.shoplens.shoplensbackend.model.AnalysisResult;

// Java utility imports
import java.util.ArrayList;  // ArrayList = a dynamic list (like Python's list)
import java.util.List;        // List = the interface that ArrayList implements

@Service  // Spring will automatically create ONE instance of this class and share it
public class ShopService {

    // This method returns sample/dummy data for now
    // On Day 3, this will call our Python Apriori script and return real data
    public List<AnalysisResult> getSampleResults() {

        // Create an empty list to hold our results
        List<AnalysisResult> results = new ArrayList<>();

        // Add sample rules — these match the patterns from your Day 1 Jupyter Notebook!
        // new AnalysisResult(antecedent, consequent, confidence, support, lift)

        results.add(new AnalysisResult(
                "ghee",    // if customer buys ghee
                "rice",    // they also buy rice
                0.92,      // 92% confidence
                0.45,      // 45% support
                1.87       // 1.87x more likely than random
        ));

        results.add(new AnalysisResult(
                "dal",
                "rice",
                0.88,
                0.41,
                1.76
        ));

        results.add(new AnalysisResult(
                "tea",
                "sugar",
                0.85,
                0.38,
                1.65
        ));

        results.add(new AnalysisResult(
                "atta",
                "oil",
                0.79,
                0.35,
                1.54
        ));

        results.add(new AnalysisResult(
                "soap",
                "biscuit",
                0.72,
                0.31,
                1.43
        ));

        // Return the full list
        return results;
    }

    // This method returns a simple status message
    // Useful to check if our backend is running
    public String getHealthStatus() {
        return "ShopLens Backend is running successfully! 🛒";
    }
}