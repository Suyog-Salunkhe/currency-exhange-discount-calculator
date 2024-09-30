package com.currency_exchange.currency_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CurrencyAppConfig {

	@Bean
	public RestTemplate restTemplateBean() {
		return new RestTemplate();
	}

}