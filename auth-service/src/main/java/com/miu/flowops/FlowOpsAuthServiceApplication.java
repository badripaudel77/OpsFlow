package com.miu.flowops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FlowOpsAuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlowOpsAuthServiceApplication.class, args);
        log.info("FlowOps Auth Service is up and running !");
	}

}
