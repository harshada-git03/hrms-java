package com.hrms.hrms_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    private String workMode; // WFH, OFFICE, ON_DUTY

    private String status; // PRESENT, ABSENT, HALF_DAY

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.workMode == null) this.workMode = "OFFICE";
    }
}
