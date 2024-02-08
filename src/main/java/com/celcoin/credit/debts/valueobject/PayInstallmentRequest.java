package com.celcoin.credit.debts.valueobject;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayInstallmentRequest {

    @NotNull
    @Digits(integer = 7, fraction = 2)
    private BigDecimal value;
}
