package com.shoplens.shoplensbackend.model;

// Lombok annotations — these automatically generate getters, setters, constructors
// Without Lombok you'd write 30+ lines of boring code manually
import lombok.AllArgsConstructor;  // Generates constructor with ALL fields
import lombok.Data;                 // Generates getters + setters + toString + equals
import lombok.NoArgsConstructor;   // Generates empty constructor (needed by Spring/JSON)

@Data                    // Automatically creates: getItem1(), setItem1(), toString(), etc.
@NoArgsConstructor       // Creates: public AnalysisResult() {}
@AllArgsConstructor      // Creates: public AnalysisResult(String antecedent, ...)
public class AnalysisResult {

    // antecedent = the "if" part of the rule
    // Example: if customer buys "ghee" → antecedent is "ghee"
    private String antecedent;

    // consequent = the "then" part of the rule
    // Example: → they also buy "rice" → consequent is "rice"
    private String consequent;

    // confidence = how often this rule is correct
    // Example: 0.92 means 92% of the time when ghee is bought, rice is also bought
    private double confidence;

    // support = how often both items appear together in ALL transactions
    // Example: 0.45 means 45% of all bills contain both items
    private double support;

    // lift = how much MORE likely they are bought together vs randomly
    // lift > 1 means positive association (good!)
    // lift = 1 means no relationship
    // lift < 1 means negative association
    private double lift;
}