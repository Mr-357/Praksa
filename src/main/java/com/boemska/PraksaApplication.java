package com.boemska;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//postgre port 5423
//pgcli -h localhost -p 5432 -u postgres
//sudo systemctl start postgresql-11.service
@SpringBootApplication (scanBasePackages = {"com.boemska"} )
public class PraksaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PraksaApplication.class, args);
	}

}
