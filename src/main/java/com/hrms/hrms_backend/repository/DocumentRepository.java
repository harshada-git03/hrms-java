package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeeId(Long employeeId);
    List<Document> findByStatus(String status);
    List<Document> findByEmployeeIdAndStatus(Long employeeId, String status);
}
