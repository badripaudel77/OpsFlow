package com.miu.flowops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FlowOpsNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowOpsNotificationApplication.class, args);
        log.info("Devops Workflow - Notification Service is Up and Running ");
	}

}
