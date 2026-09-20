package com.minimart.payment;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.minimart.api.http.CorrelationHeaders;
import com.minimart.api.http.CorrelationIdHolder;
import com.minimart.api.http.IdempotencyHeaders;
import com.minimart.order.api.ApplyPayResultCommand;
import com.minimart.order.api.OrderPayResultClient;
import com.minimart.order.api.PayResultStatus;

@SpringBootTest
class OrderPayResultClientContractTest {

	private static final WireMockServer WM = new WireMockServer(wireMockConfig().dynamicPort());

	static {
		WM.start();
	}

	@DynamicPropertySource
	static void feignUrl(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.openfeign.client.config.order-service.url", () -> "http://localhost:" + WM.port());
	}

	@Autowired
	OrderPayResultClient orderPayResultClient;

	@BeforeEach
	void reset() {
		WM.resetAll();
		CorrelationIdHolder.clear();
	}

	@AfterAll
	static void stop() {
		WM.stop();
	}

	@Test
	void payResultIsIdempotentWriteWithCorrelation() {
		CorrelationIdHolder.set("corr-result");
		WM.stubFor(post(urlEqualTo("/internal/v1/orders/11/pay-result")).willReturn(aResponse().withStatus(204)));
		orderPayResultClient.applyPayResult(11L, "pay-11-succeeded",
				new ApplyPayResultCommand(88L, PayResultStatus.SUCCEEDED, 3998L));
		WM.verify(postRequestedFor(urlEqualTo("/internal/v1/orders/11/pay-result"))
				.withHeader(IdempotencyHeaders.KEY, equalTo("pay-11-succeeded"))
				.withHeader(CorrelationHeaders.CORRELATION_ID, equalTo("corr-result"))
				.withRequestBody(matchingJsonPath("$.paymentId", equalTo("88")))
				.withRequestBody(matchingJsonPath("$.status", equalTo("SUCCEEDED")))
				.withRequestBody(matchingJsonPath("$.amountCents", equalTo("3998"))));
	}

	@Test
	void duplicateNotifyIsStillPostedOncePerCall() {
		WM.stubFor(post(urlEqualTo("/internal/v1/orders/11/pay-result")).willReturn(aResponse().withStatus(200)));
		ApplyPayResultCommand command = new ApplyPayResultCommand(88L, PayResultStatus.SUCCEEDED, 3998L);
		orderPayResultClient.applyPayResult(11L, "pay-11-succeeded", command);
		orderPayResultClient.applyPayResult(11L, "pay-11-succeeded", command);
		assertThat(WM.findAll(postRequestedFor(urlEqualTo("/internal/v1/orders/11/pay-result")))).hasSize(2);
	}
}
