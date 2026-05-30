package com.shoplens.shoplensbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NEW: Many analysis results belong to one Owner
    // @ManyToOne = "many of these rows → one owner"
    // @JoinColumn = the foreign key column in THIS table is "owner_id"
    // fetch = LAZY means JPA won't auto-load the Owner object unless you ask for it
    // This prevents accidentally loading ALL owner data on every query
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "antecedents")
    private String antecedents;
    // Example: "Bread, Butter"

    @Column(name = "consequents")
    private String consequents;
    // Example: "Jam"

    @Column(name = "support")
    private Double support;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "lift")
    private Double lift;

    // NEW: Which month and year this analysis covers
    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    // Default constructor
    public AnalysisResult() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

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

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}