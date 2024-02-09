package com.celcoin.credit.debts.repository;

import com.celcoin.credit.debts.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, JpaSpecificationExecutor<Debt> {
}
