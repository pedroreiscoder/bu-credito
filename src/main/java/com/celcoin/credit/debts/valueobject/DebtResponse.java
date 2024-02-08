package com.celcoin.credit.debts.valueobject;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DebtResponse {

    private Long id;
    private String creditorName;
    private BigDecimal totalValue;
    private BigDecimal balanceDue;
    private Integer numberOfInstallments;
    private LocalDate dueDate;
    private List<InstallmentResponse> installments;
    private Integer statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
