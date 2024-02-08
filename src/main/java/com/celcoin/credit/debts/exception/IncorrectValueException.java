package com.celcoin.credit.debts.exception;

import java.math.BigDecimal;

public class IncorrectValueException extends RuntimeException {
    public IncorrectValueException(BigDecimal value){
        super("The installment value for this debt is: " + value);
    }
}
