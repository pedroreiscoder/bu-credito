package com.celcoin.credit.debts.specification;

import com.celcoin.credit.debts.entity.Debt;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class DebtSpecification {

    private DebtSpecification(){}

    public static Specification<Debt> hasCreditorName(String creditorName){
        return (root, query, cb) -> creditorName == null ? cb.conjunction() : cb.equal(root.get("creditorName"), creditorName);
    }

    public static Specification<Debt> hasDueDate(LocalDate dueDate){
        return (root, query, cb) -> dueDate == null ? cb.conjunction() : cb.equal(root.get("dueDate"), dueDate);
    }

    public static Specification<Debt> hasStatus(Integer statusId){
        return (root, query, cb) -> {
            if(statusId == null){
                return cb.conjunction();
            }
            Path<Debt> status = root.get("status");
            return cb.equal(status.get("id"), statusId);
        };
    }
}
