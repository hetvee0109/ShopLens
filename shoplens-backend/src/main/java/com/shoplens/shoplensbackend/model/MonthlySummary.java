package com.shoplens.shoplensbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_summaries")
public class MonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "total_transactions")
    private Integer totalTransactions;

    @Column(name = "total_rules_found")
    private Integer totalRulesFound;

    @Column(name = "top_itemset")
    private String topItemset;

    @Column(name = "avg_lift")
    private Double avgLift;

    @Column(name = "avg_confidence")
    private Double avgConfidence;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public MonthlySummary() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Integer getTotalRulesFound() { return totalRulesFound; }
    public void setTotalRulesFound(Integer totalRulesFound) {
        this.totalRulesFound = totalRulesFound;
    }

    public String getTopItemset() { return topItemset; }
    public void setTopItemset(String topItemset) { this.topItemset = topItemset; }

    public Double getAvgLift() { return avgLift; }
    public void setAvgLift(Double avgLift) { this.avgLift = avgLift; }

    public Double getAvgConfidence() { return avgConfidence; }
    public void setAvgConfidence(Double avgConfidence) { this.avgConfidence = avgConfidence; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}