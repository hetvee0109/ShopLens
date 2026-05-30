package com.shoplens.shoplensbackend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity                          // tells JPA this class maps to a database table
@Table(name = "owners")          // the table is named "owners"
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY means MySQL's AUTO_INCREMENT controls the ID
    private Long id;

    @Column(name = "shop_name", nullable = false)
    private String shopName;
    // nullable = false matches NOT NULL in the SQL

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    // unique = true matches UNIQUE in the SQL

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    // We store BCrypt hash here, never plain text

    @Column(name = "phone")
    private String phone;

    @Column(name = "city")
    private String city;

    @Enumerated(EnumType.STRING)
    // EnumType.STRING stores "ADMIN" or "OWNER" as text, not 0 or 1
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive;

    // Default constructor — JPA requires this
    public Owner() {}

    // --- Getters and Setters ---
    // These let other classes read and write each field

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}