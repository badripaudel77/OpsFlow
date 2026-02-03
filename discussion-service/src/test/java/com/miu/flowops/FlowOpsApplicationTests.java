package com.miu.flowops;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration test that requires MongoDB and Kafka to be running.
 * Disabled by default - enable when running with docker-compose up
 */
@Disabled("Requires MongoDB and Kafka running - use 'docker-compose up' first")
class FlowOpsApplicationTests {

	@Test
	void contextLoads() {
	}

}
