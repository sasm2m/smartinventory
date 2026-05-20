package com.cedia.smartinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cedia.smartinventory.model.Product;

@SpringBootApplication
public class SmartinventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartinventoryApplication.class, args);
		Product laptop = Product.builder()
    .id(1L)
    .name("Laptop")
    .description("Laptop de alto rendimiento")
    .price(1200.0)
    .stock(10)
    .active(true)
    .build();

System.out.println(laptop);
	}

}
