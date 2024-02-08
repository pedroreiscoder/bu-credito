package com.celcoin.credit.debts.valueobject;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InstallmentResponse {

    private Long id;
    private BigDecimal value;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
}
