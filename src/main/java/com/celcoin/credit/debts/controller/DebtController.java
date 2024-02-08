package com.celcoin.credit.debts.controller;

import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.service.DebtService;
import com.celcoin.credit.debts.valueobject.DebtResponse;
import com.celcoin.credit.debts.valueobject.RegisterDebtRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity<DebtResponse> registerDebt(@RequestBody @Valid RegisterDebtRequest request){

        Debt debt = mapper.map(request, Debt.class);
        Debt createdDebt = debtService.registerDebt(debt);
        DebtResponse response = mapper.map(createdDebt, DebtResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
