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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DebtServiceTest {

    private DebtService debtService;
    private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(5);

    @Mock
    private DebtRepository debtRepository;

    @Mock
    private InstallmentRepository installmentRepository;

    @BeforeEach
    void setup(){
        this.debtService = new DebtService(this.debtRepository,
                                           this.installmentRepository,
                                           INTEREST_RATE);
    }

    @Test
    void getDebts_WhenCalled_ReturnsDebts(){

        List<Debt> debts = List.of(new Debt(), new Debt(), new Debt());

        when(debtRepository.findAll(ArgumentMatchers.<Specification<Debt>>any())).thenReturn(debts);

        List<Debt> result = debtService.getDebts(null, null, null);

        assertEquals(debts.size(), result.size());
    }

    @Test
    void getDebt_DebtDoesNotExist_ThrowsDebtNotFoundException(){

        Long id = 1L;

        when(debtRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DebtNotFoundException.class, () -> debtService.getDebt(id));
    }

    @Test
    void getDebt_DebtExists_ReturnsDebt(){

        Long id = 1L;

        Debt debt = new Debt();
        debt.setId(id);

        when(debtRepository.findById(id)).thenReturn(Optional.of(debt));

        Debt result = debtService.getDebt(id);

        assertEquals(debt.getId(), result.getId());
    }

    @Test
    void registerDebt_WhenCalled_ReturnsCreatedDebt(){

        Debt debt = new Debt();
        debt.setCreditorName("Pedro");
        debt.setTotalValue(BigDecimal.valueOf(600));
        debt.setNumberOfInstallments(3);
        debt.setDueDate(LocalDate.of(2024, 3, 9));

        when(debtRepository.save(any(Debt.class))).thenAnswer(returnsFirstArg());

        Debt result = debtService.registerDebt(debt);

        assertNotNull(result.getStatus());
        assertNotNull(result.getBalanceDue());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void payInstallment_DebtDoesNotExist_ThrowsDebtNotFoundException(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(200));

        when(debtRepository.findById(debtId)).thenReturn(Optional.empty());

        assertThrows(DebtNotFoundException.class, () -> debtService.payInstallment(debtId, installment));
    }

    @Test
    void payInstallment_DebtAlreadyPaid_ThrowsDebtAlreadyPaidException(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(200));

        Status status = new Status();
        status.setId(DebtStatus.PAID);

        Debt debt = new Debt();
        debt.setId(debtId);
        debt.setStatus(status);

        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));

        assertThrows(DebtAlreadyPaidException.class, () -> debtService.payInstallment(debtId, installment));
    }

    @Test
    void payInstallment_DebtOverDue_ThrowsDebtOverdueException(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(200));

        Status status = new Status();
        status.setId(DebtStatus.CREATED);

        Debt debt = new Debt();
        debt.setId(debtId);
        debt.setStatus(status);
        debt.setDueDate(LocalDate.of(2023,4, 25));
        debt.setNumberOfInstallments(3);
        debt.setTotalValue(BigDecimal.valueOf(600));

        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));

        assertThrows(DebtOverdueException.class, () -> debtService.payInstallment(debtId, installment));
    }

    @Test
    void payInstallment_IncorrectValue_ThrowsIncorrectValueException(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(150));

        Status status = new Status();
        status.setId(DebtStatus.CREATED);

        Debt debt = new Debt();
        debt.setId(debtId);
        debt.setStatus(status);
        debt.setDueDate(LocalDate.of(2030,4, 25));
        debt.setNumberOfInstallments(3);
        debt.setTotalValue(BigDecimal.valueOf(600));

        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));

        assertThrows(IncorrectValueException.class, () -> debtService.payInstallment(debtId, installment));
    }

    @Test
    void payInstallment_BalanceDueNotZero_ReturnsStatusPartiallyPaid(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(200));

        Status status = new Status();
        status.setId(DebtStatus.CREATED);

        Debt debt = new Debt();
        debt.setId(debtId);
        debt.setStatus(status);
        debt.setDueDate(LocalDate.of(2030,4, 25));
        debt.setNumberOfInstallments(3);
        debt.setTotalValue(BigDecimal.valueOf(600));
        debt.setBalanceDue(BigDecimal.valueOf(600));

        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenAnswer(returnsFirstArg());
        when(installmentRepository.save(any(Installment.class))).thenAnswer(returnsFirstArg());

        Installment result = debtService.payInstallment(debtId, installment);

        assertNotNull(result.getDebt());
        assertEquals(BigDecimal.valueOf(400).setScale(2, RoundingMode.HALF_EVEN), result.getDebt().getBalanceDue());
        assertNotNull(result.getDebt().getStatus());
        assertEquals(DebtStatus.PARTIALLY_PAID, result.getDebt().getStatus().getId());
        assertNotNull(result.getDebt().getUpdatedAt());
        assertNotNull(result.getInterestRate());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void payInstallment_BalanceDueZero_ReturnsStatusPaid(){

        Long debtId = 1L;

        Installment installment = new Installment();
        installment.setValue(BigDecimal.valueOf(200));

        Status status = new Status();
        status.setId(DebtStatus.PARTIALLY_PAID);

        Debt debt = new Debt();
        debt.setId(debtId);
        debt.setStatus(status);
        debt.setDueDate(LocalDate.of(2030,4, 25));
        debt.setNumberOfInstallments(3);
        debt.setTotalValue(BigDecimal.valueOf(600));
        debt.setBalanceDue(BigDecimal.valueOf(200));

        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenAnswer(returnsFirstArg());
        when(installmentRepository.save(any(Installment.class))).thenAnswer(returnsFirstArg());

        Installment result = debtService.payInstallment(debtId, installment);

        assertNotNull(result.getDebt());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), result.getDebt().getBalanceDue());
        assertNotNull(result.getDebt().getStatus());
        assertEquals(DebtStatus.PAID, result.getDebt().getStatus().getId());
        assertNotNull(result.getDebt().getUpdatedAt());
        assertNotNull(result.getInterestRate());
        assertNotNull(result.getCreatedAt());
    }
}
