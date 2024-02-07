package com.celcoin.credit.debts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "debts")
public class Debt {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "creditor_name")
    private String creditorName;

    @Column(name = "total_value")
    private BigDecimal totalValue;

    @Column(name = "number_of_installments")
    private Integer numberOfInstallments;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @OneToMany(mappedBy="debt")
    private List<Installment> installments;

    @Column(name = "balance_due")
    private BigDecimal balanceDue;

    @OneToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
