package com.shoplens.shoplensbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_suggestions")
public class DiscountSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "itemset", nullable = false)
    private String itemset;
    // Example: "Bread, Butter, Jam"

    @Column(name = "suggested_discount_pct", nullable = false)
    private Integer suggestedDiscountPct;
    // Example: 15 means "suggest 15% off this bundle"

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    // Example: "High lift (3.2) indicates strong association"

    @Column(name = "based_on_lift")
    private Double basedOnLift;

    @Column(name = "based_on_confidence")
    private Double basedOnConfidence;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor
    public DiscountSuggestion() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public String getItemset() { return itemset; }
    public void setItemset(String itemset) { this.itemset = itemset; }

    public Integer getSuggestedDiscountPct() { return suggestedDiscountPct; }
    public void setSuggestedDiscountPct(Integer suggestedDiscountPct) {
        this.suggestedDiscountPct = suggestedDiscountPct;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getBasedOnLift() { return basedOnLift; }
    public void setBasedOnLift(Double basedOnLift) { this.basedOnLift = basedOnLift; }

    public Double getBasedOnConfidence() { return basedOnConfidence; }
    public void setBasedOnConfidence(Double basedOnConfidence) {
        this.basedOnConfidence = basedOnConfidence;
    }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}