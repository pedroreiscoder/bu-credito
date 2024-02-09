package com.celcoin.credit.error;

import com.celcoin.credit.debts.exception.DebtAlreadyPaidException;
import com.celcoin.credit.debts.exception.DebtNotFoundException;
import com.celcoin.credit.debts.exception.DebtOverdueException;
import com.celcoin.credit.debts.exception.IncorrectValueException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BuCreditExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request){

        List<String> errors = e.getBindingResult()
                               .getFieldErrors()
                               .stream()
                               .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                               .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DebtNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDebtNotFoundException(DebtNotFoundException e){

        List<String> errors = List.of(e.getMessage());
        ErrorResponse response = new ErrorResponse(errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DebtAlreadyPaidException.class)
    public ResponseEntity<ErrorResponse> handleDebtAlreadyPaidException(DebtAlreadyPaidException e){

        List<String> errors = List.of(e.getMessage());
        ErrorResponse response = new ErrorResponse(errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DebtOverdueException.class)
    public ResponseEntity<ErrorResponse> handleDebtOverdueException(DebtOverdueException e){

        List<String> errors = List.of(e.getMessage());
        ErrorResponse response = new ErrorResponse(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IncorrectValueException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectValueException(IncorrectValueException e){

        List<String> errors = List.of(e.getMessage());
        ErrorResponse response = new ErrorResponse(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
