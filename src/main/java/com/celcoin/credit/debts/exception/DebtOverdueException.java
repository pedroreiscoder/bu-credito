package com.celcoin.credit.debts.exception;

import java.math.BigDecimal;

public class DebtOverdueException extends RuntimeException {
    public DebtOverdueException(BigDecimal value, BigDecimal interestRate){
        super("This debt is overdue, the new value of the installment is " +
                value + " with an interest rate of " + interestRate + "%");
    }
}
