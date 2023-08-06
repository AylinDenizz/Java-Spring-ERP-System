package com.allianz.erpsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication

//creating date and updating date comes with this anotation
@EnableJpaAuditing
public class ErpSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpSystemApplication.class, args);
    }

}
