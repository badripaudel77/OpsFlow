package com.miu.flowops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@Slf4j
public class FlowOpsApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowOpsApiGatewayApplication.class, args);
        log.info("FlowOps : API Gateway is up and running !");
	}

}
