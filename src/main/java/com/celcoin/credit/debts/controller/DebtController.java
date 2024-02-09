package com.celcoin.credit.debts.controller;

import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.entity.Installment;
import com.celcoin.credit.debts.service.DebtService;
import com.celcoin.credit.debts.valueobject.DebtResponse;
import com.celcoin.credit.debts.valueobject.InstallmentResponse;
import com.celcoin.credit.debts.valueobject.PayInstallmentRequest;
import com.celcoin.credit.debts.valueobject.RegisterDebtRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<List<DebtResponse>> getDebts(@RequestParam(required = false) String creditorName,
                                                       @RequestParam(required = false) LocalDate dueDate,
                                                       @RequestParam(required = false) Integer statusId){

        List<Debt> debts = debtService.getDebts(creditorName, dueDate, statusId);
        List<DebtResponse> response = debts.stream()
                .map(debt -> mapper.map(debt, DebtResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtResponse> getDebt(@PathVariable Long id){

        Debt debt = debtService.getDebt(id);
        DebtResponse response = mapper.map(debt, DebtResponse.class);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DebtResponse> registerDebt(@RequestBody @Valid RegisterDebtRequest request){

        Debt debt = mapper.map(request, Debt.class);
        Debt createdDebt = debtService.registerDebt(debt);
        DebtResponse response = mapper.map(createdDebt, DebtResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{debtId}/installments")
    public ResponseEntity<InstallmentResponse> payInstallment(@PathVariable Long debtId,
                                                              @RequestBody @Valid PayInstallmentRequest request){

        Installment installment = mapper.map(request, Installment.class);
        Installment createdInstallment = debtService.payInstallment(debtId, installment);
        InstallmentResponse response = mapper.map(createdInstallment, InstallmentResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
