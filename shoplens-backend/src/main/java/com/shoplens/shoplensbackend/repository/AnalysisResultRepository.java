package com.shoplens.shoplensbackend.repository;

// Import our Entity class
import com.shoplens.shoplensbackend.model.AnalysisResult;

// JpaRepository is the Spring interface that gives us free DB methods
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository marks this as a Spring-managed database component
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // We write NOTHING inside this interface body.
    //
    // Just by extending JpaRepository<AnalysisResult, Long>, we get:
    //
    //   save(AnalysisResult entity)          → INSERT or UPDATE a row
    //   findAll()                            → SELECT * FROM analysis_results
    //   findById(Long id)                    → SELECT * WHERE id = ?
    //   deleteById(Long id)                  → DELETE WHERE id = ?
    //   count()                              → SELECT COUNT(*) FROM ...
    //   existsById(Long id)                  → check if a row exists
    //
    // Spring Data JPA generates the actual SQL implementation automatically.
    // This is the magic of Spring — write an interface, get a working class.
}