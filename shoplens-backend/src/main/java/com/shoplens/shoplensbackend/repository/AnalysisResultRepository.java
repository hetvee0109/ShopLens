package com.shoplens.shoplensbackend.repository;



import com.shoplens.shoplensbackend.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // Owner queries — for the owner dashboard
    // SELECT * FROM analysis_results WHERE owner_id = ?
    List<AnalysisResult> findByOwnerId(Long ownerId);

    // SELECT * FROM analysis_results WHERE owner_id = ? AND month = ? AND year = ?
    List<AnalysisResult> findByOwnerIdAndMonthAndYear(Long ownerId, int month, int year);

    // Admin query — returns ALL rows from ALL owners
    // JpaRepository already has findAll() built in, so nothing extra needed here
    // (we use the inherited findAll() in AdminService)
}