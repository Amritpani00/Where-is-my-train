package com.traintracker.api.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {
	@Bean
	@ConditionalOnProperty(prefix = "razorpay", name = {"keyId", "keySecret"})
	public RazorpayClient razorpayClient(
		@Value("${razorpay.keyId}") String keyId,
		@Value("${razorpay.keySecret}") String keySecret
	) throws Exception {
		return new RazorpayClient(keyId, keySecret);
	}
}