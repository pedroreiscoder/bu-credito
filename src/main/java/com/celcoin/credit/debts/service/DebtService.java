package com.celcoin.credit.debts.service;

import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.entity.Status;
import com.celcoin.credit.debts.repository.DebtRepository;
import com.celcoin.credit.debts.valueobject.DebtStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;

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
}
