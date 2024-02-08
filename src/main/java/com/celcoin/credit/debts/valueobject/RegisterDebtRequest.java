package com.celcoin.credit.debts.valueobject;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterDebtRequest {

    @NotNull
    @Size(max = 100)
    private String creditorName;

    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal totalValue;

    @NotNull
    private Integer numberOfInstallments;

    @NotNull
    private LocalDate dueDate;
}
