package com.shoplens.shoplensbackend.repository;

import com.shoplens.shoplensbackend.model.DiscountSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscountSuggestionRepository extends JpaRepository<DiscountSuggestion, Long> {

    // Owner: get their discount suggestions for a specific month
    List<DiscountSuggestion> findByOwnerIdAndMonthAndYear(Long ownerId, int month, int year);

    // Owner: get all their suggestions across all months
    List<DiscountSuggestion> findByOwnerId(Long ownerId);

    // Admin: findAll() is inherited
}