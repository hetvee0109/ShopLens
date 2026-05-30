package com.shoplens.shoplensbackend.repository;

import com.shoplens.shoplensbackend.model.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {

    // Get all monthly summaries for one owner
    List<MonthlySummary> findByOwnerId(Long ownerId);

    // Get summary for one specific month (to check if it already exists)
    // Optional because it might not exist yet
    Optional<MonthlySummary> findByOwnerIdAndMonthAndYear(Long ownerId, int month, int year);

    // Admin: findAll() is inherited
}