package com.celcoin.credit.debts.service;

import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.entity.Installment;
import com.celcoin.credit.debts.entity.Status;
import com.celcoin.credit.debts.exception.DebtAlreadyPaidException;
import com.celcoin.credit.debts.exception.DebtNotFoundException;
import com.celcoin.credit.debts.exception.DebtOverdueException;
import com.celcoin.credit.debts.exception.IncorrectValueException;
import com.celcoin.credit.debts.repository.DebtRepository;
import com.celcoin.credit.debts.repository.InstallmentRepository;
import com.celcoin.credit.debts.valueobject.DebtStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DebtService {

    private final BigDecimal interestRate;
    private final DebtRepository debtRepository;
    private final InstallmentRepository installmentRepository;

    public DebtService(DebtRepository debtRepository,
                       InstallmentRepository installmentRepository,
                       @Value("${bu-credit.interest-rate}") BigDecimal interestRate){
        this.interestRate = interestRate;
        this.debtRepository = debtRepository;
        this.installmentRepository = installmentRepository;
    }

    @Transactional
    public Debt registerDebt(Debt debt){

        Status status = new Status();
        status.setId(DebtStatus.CREATED);

        debt.setStatus(status);
        debt.setBalanceDue(debt.getTotalValue());
        debt.setCreatedAt(LocalDateTime.now());
        debt.setUpdatedAt(LocalDateTime.now());

        return debtRepository.save(debt);
    }

    @Transactional
    public Installment payInstallment(Long debtId, Installment installment){

        Debt debt = debtRepository.findById(debtId).orElseThrow(DebtNotFoundException::new);

        if(debt.getStatus() != null && debt.getStatus().getId().equals(DebtStatus.PAID)){
            throw new DebtAlreadyPaidException();
        }

        LocalDate today = LocalDate.now();
        BigDecimal finalInterestRate = BigDecimal.ZERO;

        if(today.isAfter(debt.getDueDate())){
            finalInterestRate = this.interestRate;
        }

        BigDecimal numberOfInstallments = BigDecimal.valueOf(debt.getNumberOfInstallments());
        BigDecimal installmentValue = debt.getTotalValue().divide(numberOfInstallments, 2, RoundingMode.HALF_EVEN);

        BigDecimal decimalInterestRate = finalInterestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
        BigDecimal multiplier = decimalInterestRate.add(BigDecimal.ONE);

        BigDecimal finalInstallmentValue = installmentValue.multiply(multiplier);

        if(installment.getValue().compareTo(finalInstallmentValue) != 0){
            if(today.isAfter(debt.getDueDate())){
                throw new DebtOverdueException(finalInstallmentValue, finalInterestRate);
            }
            throw new IncorrectValueException(finalInstallmentValue);
        }

        BigDecimal newBalanceDue = debt.getBalanceDue().subtract(installmentValue);
        Status status = new Status();

        if(newBalanceDue.compareTo(BigDecimal.ZERO) == 0){
            status.setId(DebtStatus.PAID);
        }else{
            status.setId(DebtStatus.PARTIALLY_PAID);
        }

        debt.setBalanceDue(newBalanceDue);
        debt.setStatus(status);
        debt.setUpdatedAt(LocalDateTime.now());

        debtRepository.save(debt);

        installment.setInterestRate(finalInterestRate);
        installment.setDebt(debt);
        installment.setCreatedAt(LocalDateTime.now());

        return installmentRepository.save(installment);
    }
}
