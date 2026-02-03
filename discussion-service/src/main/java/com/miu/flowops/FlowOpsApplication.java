package com.miu.flowops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FlowOpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowOpsApplication.class, args);
        log.info("Discussion Service is Up and Running");
	}

}
