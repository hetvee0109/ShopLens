package com.shoplens.shoplensbackend.model;

import java.util.List;

// This class represents ONE association rule
// Example: {milk} → {bread} with support=0.05, confidence=0.8, lift=2.1
public class AnalysisResult {

    // "antecedents" = the LEFT side of the rule — items the customer already has
    private List<String> antecedents;

    // "consequents" = the RIGHT side — items we recommend
    private List<String> consequents;

    // "support" = how often this combination appears in ALL transactions
    private double support;

    // "confidence" = given antecedents, how often do consequents appear too
    private double confidence;

    // "lift" = how much better than random chance (lift > 1 means meaningful)
    private double lift;

    // ── Constructors ──

    // Empty constructor — Spring needs this to create objects automatically
    public AnalysisResult() {}

    // Full constructor — used when we create objects manually
    public AnalysisResult(List<String> antecedents, List<String> consequents,
                          double support, double confidence, double lift) {
        this.antecedents = antecedents;
        this.consequents = consequents;
        this.support = support;
        this.confidence = confidence;
        this.lift = lift;
    }

    // ── Getters and Setters ──
    // Spring Boot uses these to convert Java objects to JSON automatically

    public List<String> getAntecedents() { return antecedents; }
    public void setAntecedents(List<String> antecedents) { this.antecedents = antecedents; }

    public List<String> getConsequents() { return consequents; }
    public void setConsequents(List<String> consequents) { this.consequents = consequents; }

    public double getSupport() { return support; }
    public void setSupport(double support) { this.support = support; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public double getLift() { return lift; }
    public void setLift(double lift) { this.lift = lift; }
}