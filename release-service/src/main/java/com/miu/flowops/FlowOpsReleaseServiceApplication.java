package com.miu.flowops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class FlowOpsReleaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowOpsReleaseServiceApplication.class, args);
        log.info("Devops Workflow is Up and Running ");
	}

}
