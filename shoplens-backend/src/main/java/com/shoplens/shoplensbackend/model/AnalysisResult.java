package com.shoplens.shoplensbackend.model;

// JPA annotations — these tell Hibernate how to map this class to MySQL
import jakarta.persistence.*;

// For the timestamp column — records when each analysis was run
import java.time.LocalDateTime;

@Entity
// @Entity tells Hibernate: "This class = a database table"

@Table(name = "analysis_results")
// @Table tells Hibernate: "Name the table 'analysis_results' in MySQL"
// Without this, Hibernate would name it 'AnalysisResult' (class name)

public class AnalysisResult {

    @Id
    // @Id marks this field as the PRIMARY KEY of the table
    // Primary key = unique identifier for each row (like a row number)

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue tells MySQL to auto-increment this number
    // So first row gets id=1, second gets id=2, etc.
    // You never need to set this yourself — MySQL handles it
    private Long id;

    @Column(name = "antecedents", columnDefinition = "TEXT")
    // @Column lets you customize the column name and type
    // TEXT = can store long strings (longer than VARCHAR's 255 limit)
    // antecedents = the "if" part of the rule, e.g. "bread, butter"
    private String antecedents;

    @Column(name = "consequents")
    // consequents = the "then" part of the rule, e.g. "milk"
    private String consequents;

    @Column(name = "support")
    // support = fraction of all transactions that contain this rule
    // e.g. 0.4 means 40% of all orders had these items together
    private Double support;

    @Column(name = "confidence")
    // confidence = how often the rule is correct
    // e.g. 0.8 means when antecedent is bought, consequent bought 80% of time
    private Double confidence;

    @Column(name = "lift")
    // lift = how much better than random chance this rule is
    // lift > 1 means the items are genuinely associated
    private Double lift;

    @Column(name = "created_at")
    // created_at = timestamp of when this analysis was saved
    private LocalDateTime createdAt;

    // ── Constructor called before saving to DB ──────────────
    @PrePersist
    // @PrePersist = "run this method automatically just before saving to MySQL"
    // This ensures createdAt is always filled in — you never forget to set it
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        // LocalDateTime.now() = current date and time on your system
    }

    // ── Getters and Setters ──────────────────────────────────
    // Getters = methods to READ the value of a private field
    // Setters = methods to WRITE/change the value of a private field
    // Spring needs these to serialize/deserialize JSON automatically

    public Long getId() { return id; }

    public String getAntecedents() { return antecedents; }
    public void setAntecedents(String antecedents) { this.antecedents = antecedents; }

    public String getConsequents() { return consequents; }
    public void setConsequents(String consequents) { this.consequents = consequents; }

    public Double getSupport() { return support; }
    public void setSupport(Double support) { this.support = support; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Double getLift() { return lift; }
    public void setLift(Double lift) { this.lift = lift; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}