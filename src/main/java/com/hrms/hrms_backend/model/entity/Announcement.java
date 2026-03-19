package com.hrms.hrms_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    private String targetAudience; // ALL, EMPLOYEE, HR_MANAGER, ADMIN

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.targetAudience == null) this.targetAudience = "ALL";
    }
}
