package com.celcoin.credit.debts.exception;

public class DebtAlreadyPaidException extends RuntimeException {
    public DebtAlreadyPaidException(){
        super("Debt already paid");
    }
}
