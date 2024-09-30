package com.currency_exchange.currency_application.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.currency_exchange.currency_application.response.ExchangeRateResponse;

@Service
public class ExchangeRateService {

	private final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

	@Value("${api.url}")
	private String API_URL;

	@Value("${api.key}")
	private String API_KEY;

	@Autowired
	private RestTemplate restTemplate;

	public BigDecimal getExchangeRateForCurrency(String baseCurrency, String targetCurrency) {

		ExchangeRateResponse response = getLiveExchangeRate(baseCurrency, targetCurrency);

		Long currentTime = System.currentTimeMillis();
		Long nextUpdate = response.getTime_next_update_unix() * 1000;

		if (currentTime >= nextUpdate) {
			removeLiveExchangeRateForCurrency(baseCurrency);
			response = getLiveExchangeRate(baseCurrency, targetCurrency);
		}

		if (response != null && response.getRates().containsKey(targetCurrency)) {
			return response.getRates().get(targetCurrency);
		} else {
			logger.error("getExchangeRateForCurrency() - baseCurrency : {}, targetCurrency: {}", baseCurrency, targetCurrency );
			throw new RuntimeException("Failed to get exchange rate");
		}
	}

	@Cacheable(value = "exchangeRate", key = "#baseCurrency")
	public ExchangeRateResponse getLiveExchangeRate(String baseCurrency, String targetCurrency) {

		String url = API_URL + baseCurrency + "?apikey=" + API_KEY;

		ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

		return response;
	}

	@CacheEvict(value = "exchangeRate", key = "#baseCurrency")
	public void removeLiveExchangeRateForCurrency(String baseCurrency) {
	}

}
