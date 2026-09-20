package com.minimart.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import com.minimart.api.autoconfigure.MinimartFeignConfiguration;
import com.minimart.order.api.OrderPayResultClient;

@SpringBootApplication
@Import(MinimartFeignConfiguration.class)
@EnableFeignClients(clients = OrderPayResultClient.class)
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
