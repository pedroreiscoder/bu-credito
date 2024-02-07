package com.celcoin.credit.debts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "installments")
public class Installment {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
