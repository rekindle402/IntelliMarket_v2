package com.intellimarket.intellimarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IntelliMarketV2Application {

    public static void main(String[] args) {
        SpringApplication.run(IntelliMarketV2Application.class, args);
    }

}
