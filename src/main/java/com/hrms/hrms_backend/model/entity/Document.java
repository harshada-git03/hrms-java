package com.hrms.hrms_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String title;

    private String type; // OFFER_LETTER, NDA, APPRAISAL, OTHER

    private String status; // PENDING_SIGNATURE, SIGNED, REJECTED

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private Employee requestedBy;

    private String fileUrl; // link to actual document

    private String remarks;

    @Column(updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime signedAt;

    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING_SIGNATURE";
    }
}
