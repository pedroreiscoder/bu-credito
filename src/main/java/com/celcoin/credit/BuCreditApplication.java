package com.celcoin.credit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "BU Credit API"))
public class BuCreditApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuCreditApplication.class, args);
	}

}
