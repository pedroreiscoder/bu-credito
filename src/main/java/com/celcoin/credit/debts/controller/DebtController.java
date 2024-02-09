package com.celcoin.credit.debts.controller;

import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.entity.Installment;
import com.celcoin.credit.debts.service.DebtService;
import com.celcoin.credit.debts.valueobject.DebtResponse;
import com.celcoin.credit.debts.valueobject.InstallmentResponse;
import com.celcoin.credit.debts.valueobject.PayInstallmentRequest;
import com.celcoin.credit.debts.valueobject.RegisterDebtRequest;
import com.celcoin.credit.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Debts")
@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;
    private final ModelMapper mapper;

    @Operation(summary = "Get a list of all debts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the debts",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DebtResponse.class)))})
    })
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

    @Operation(summary = "Get the debt with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the debt",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DebtResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Debt not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<DebtResponse> getDebt(@PathVariable Long id){

        Debt debt = debtService.getDebt(id);
        DebtResponse response = mapper.map(debt, DebtResponse.class);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registers a new debt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Debt was created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DebtResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping
    public ResponseEntity<DebtResponse> registerDebt(@RequestBody @Valid RegisterDebtRequest request){

        Debt debt = mapper.map(request, Debt.class);
        Debt createdDebt = debtService.registerDebt(debt);
        DebtResponse response = mapper.map(createdDebt, DebtResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Pays a new installment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Installment payment was successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InstallmentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Request/Incorrect Value/Debt Overdue",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Debt not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409", description = "Debt already paid",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/{debtId}/installments")
    public ResponseEntity<InstallmentResponse> payInstallment(@PathVariable Long debtId,
                                                              @RequestBody @Valid PayInstallmentRequest request){

        Installment installment = mapper.map(request, Installment.class);
        Installment createdInstallment = debtService.payInstallment(debtId, installment);
        InstallmentResponse response = mapper.map(createdInstallment, InstallmentResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
