package com.shoplens.shoplensbackend.repository;

import com.shoplens.shoplensbackend.model.CsvUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CsvUploadRepository extends JpaRepository<CsvUpload, Long> {

    // Get uploads for one owner, newest first
    // "OrderByUploadedAtDesc" → ORDER BY uploaded_at DESC
    List<CsvUpload> findByOwnerIdOrderByUploadedAtDesc(Long ownerId);

    // Admin: findAll() is inherited
}