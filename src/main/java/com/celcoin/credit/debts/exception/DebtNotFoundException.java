package com.celcoin.credit.debts.exception;

public class DebtNotFoundException extends RuntimeException {
    public DebtNotFoundException(){
        super("Debt not found");
    }
}
