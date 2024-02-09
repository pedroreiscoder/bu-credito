package com.celcoin.credit.debts.controller;

import com.celcoin.credit.debts.configuration.MapperConfiguration;
import com.celcoin.credit.debts.entity.Debt;
import com.celcoin.credit.debts.entity.Installment;
import com.celcoin.credit.debts.exception.DebtAlreadyPaidException;
import com.celcoin.credit.debts.exception.DebtNotFoundException;
import com.celcoin.credit.debts.exception.DebtOverdueException;
import com.celcoin.credit.debts.exception.IncorrectValueException;
import com.celcoin.credit.debts.service.DebtService;
import com.celcoin.credit.debts.valueobject.PayInstallmentRequest;
import com.celcoin.credit.debts.valueobject.RegisterDebtRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MapperConfiguration.class)
@WebMvcTest(DebtController.class)
public class DebtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DebtService debtService;

    @Test
    void getDebts_WhenCalled_ReturnsOk() throws Exception {

        List<Debt> debts = List.of(new Debt(), new Debt(), new Debt());

        when(debtService.getDebts(null, null, null)).thenReturn(debts);

        mockMvc.perform(get("/api/debts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getDebt_DebtDoesNotExist_ReturnsNotFound() throws Exception {

        Long id = 1L;

        when(debtService.getDebt(id)).thenThrow(new DebtNotFoundException());

        mockMvc.perform(get("/api/debts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDebt_DebtExists_ReturnsOk() throws Exception {

        Long id = 1L;

        Debt debt = new Debt();
        debt.setId(id);
        debt.setCreditorName("Pedro");

        when(debtService.getDebt(id)).thenReturn(debt);

        mockMvc.perform(get("/api/debts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditorName").value("Pedro"));
    }

    @Test
    void registerDebt_InvalidRequest_ReturnsBadRequest() throws Exception {

        var request = new RegisterDebtRequest();
        request.setCreditorName("Pedro");
        request.setNumberOfInstallments(3);

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDebt_ValidRequest_ReturnsCreated() throws Exception {

        var request = new RegisterDebtRequest();
        request.setCreditorName("Pedro");
        request.setTotalValue(BigDecimal.valueOf(600));
        request.setNumberOfInstallments(3);
        request.setDueDate(LocalDate.of(2024, 5, 15));

        when(debtService.registerDebt(any(Debt.class))).thenAnswer(returnsFirstArg());

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.creditorName").value("Pedro"));
    }

    @Test
    void payInstallment_InvalidRequest_ReturnsBadRequest() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void payInstallment_DebtNotFound_ReturnsNotFound() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();
        request.setValue(BigDecimal.valueOf(200));

        when(debtService.payInstallment(eq(debtId), any(Installment.class))).thenThrow(new DebtNotFoundException());

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void payInstallment_DebtAlreadyPaid_ReturnsConflict() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();
        request.setValue(BigDecimal.valueOf(200));

        when(debtService.payInstallment(eq(debtId), any(Installment.class))).thenThrow(new DebtAlreadyPaidException());

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isConflict());
    }

    @Test
    void payInstallment_DebtOverDue_ReturnsBadRequest() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();
        request.setValue(BigDecimal.valueOf(200));

        when(debtService.payInstallment(eq(debtId), any(Installment.class))).thenThrow(new DebtOverdueException(BigDecimal.valueOf(210), BigDecimal.valueOf(5)));

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void payInstallment_IncorrectValue_ReturnsBadRequest() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();
        request.setValue(BigDecimal.valueOf(150));

        when(debtService.payInstallment(eq(debtId), any(Installment.class))).thenThrow(new IncorrectValueException(BigDecimal.valueOf(200)));

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void payInstallment_ValidRequest_ReturnsCreated() throws Exception {

        Long debtId = 1L;
        var request = new PayInstallmentRequest();
        request.setValue(BigDecimal.valueOf(200));

        when(debtService.payInstallment(eq(debtId), any(Installment.class))).thenAnswer(returnsSecondArg());

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/debts/{debtId}/installments", debtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.value").value(200.00));
    }
}
