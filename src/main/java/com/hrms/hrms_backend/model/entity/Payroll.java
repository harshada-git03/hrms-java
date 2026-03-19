package com.hrms.hrms_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payroll")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int month;
    private int year;

    private double basicSalary;
    private double hra;           // House Rent Allowance
    private double allowances;
    private double deductions;
    private double netSalary;

    private String status;        // GENERATED, PAID

    @Column(updatable = false)
    private LocalDateTime generatedAt;

    private LocalDate paidOn;

    @PrePersist
    public void prePersist() {
        this.generatedAt = LocalDateTime.now();
        this.status = "GENERATED";
        // Auto calculate net salary
        this.netSalary = this.basicSalary + this.hra +
                         this.allowances - this.deductions;
    }
}
