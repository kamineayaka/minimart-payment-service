package com.minimart.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.minimart.api.http.CorrelationHeaders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void healthPropagatesCorrelationId() throws Exception {
		mockMvc.perform(get("/actuator/health").header(CorrelationHeaders.CORRELATION_ID, "pay-corr"))
				.andExpect(status().isOk())
				.andExpect(header().string(CorrelationHeaders.CORRELATION_ID, "pay-corr"));
	}

	@Test
	void laptopYamlMarksNacosOptionalAndRuntimeRequiresIt() throws Exception {
		String laptop = Files.readString(Path.of("src/main/resources/application.yaml"));
		assertThat(laptop).contains("optional:nacos:minimart-common.yaml?group=MINIMART");
		String runtime = Files.readString(Path.of("src/main/resources/application-runtime.yaml"));
		assertThat(runtime).doesNotContain("optional:nacos");
	}
}
