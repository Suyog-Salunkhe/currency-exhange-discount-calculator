package com.currency_exchange.currency_application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.currency_exchange.currency_application.response.ExchangeRateResponse;

public class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(exchangeRateService, "API_URL", "https://example.com/api/latest/");
        ReflectionTestUtils.setField(exchangeRateService, "API_KEY", "test-api-key");
    }

    @Test
    public void testGetExchangeRateForCurrency_Success() {
        // Given
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setTime_next_update_unix(System.currentTimeMillis() / 1000 + 3600); // future update time
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put(targetCurrency, new BigDecimal("0.85"));
        mockResponse.setRates(rates);

        // Mocking getLiveExchangeRate
        when(exchangeRateService.getLiveExchangeRate(baseCurrency, targetCurrency)).thenReturn(mockResponse);

        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);
        
        // When
        BigDecimal exchangeRate = exchangeRateService.getExchangeRateForCurrency(baseCurrency, targetCurrency);

        // Then
        assertNotNull(exchangeRate);
        assertEquals(new BigDecimal("0.85"), exchangeRate);
    }

    @Test
    public void testGetExchangeRateForCurrency_ExpiredCache() {
        // Given
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        ExchangeRateResponse expiredResponse = new ExchangeRateResponse();
        expiredResponse.setTime_next_update_unix(System.currentTimeMillis() / 1000 - 3600); // expired update time
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put(targetCurrency, new BigDecimal("0.85"));
        expiredResponse.setBase_code(baseCurrency);
        expiredResponse.setRates(rates);

        ExchangeRateResponse newResponse = new ExchangeRateResponse();
        newResponse.setTime_next_update_unix(System.currentTimeMillis() / 1000 + 3600); // future update time
        //newResponse.getRates().put(targetCurrency, new BigDecimal("0.90"));
        
        Map<String, BigDecimal> newRates = new HashMap<>();
        newRates.put(targetCurrency, new BigDecimal("0.90"));
        newResponse.setBase_code(targetCurrency);
        newResponse.setRates(newRates);

        // Mocking getLiveExchangeRate with expired and updated response
        when(exchangeRateService.getLiveExchangeRate(baseCurrency, targetCurrency))
            .thenReturn(expiredResponse)
            .thenReturn(newResponse);

        // When
        BigDecimal exchangeRate = exchangeRateService.getExchangeRateForCurrency(baseCurrency, targetCurrency);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(expiredResponse).thenReturn(newResponse);

        // Then
        assertNotNull(exchangeRate);
        assertEquals(new BigDecimal("0.90"), exchangeRate); // Updated exchange rate after cache eviction
    }

    @Test
    public void testGetExchangeRateForCurrency_Failure() {
        // Given
        String baseCurrency = "USD"; 
        String targetCurrency = "INR";
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setTime_next_update_unix(System.currentTimeMillis() / 1000 + 3600); // future update time
        
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.85"));
        mockResponse.setBase_code(baseCurrency);
        mockResponse.setRates(rates);
        
        
        // Mocking getLiveExchangeRate with a response that does not contain the target currency
        when(exchangeRateService.getLiveExchangeRate(baseCurrency, targetCurrency)).thenReturn(mockResponse);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            exchangeRateService.getExchangeRateForCurrency(baseCurrency, targetCurrency);
        });

        assertEquals("Failed to get exchange rate", exception.getMessage());
    }

    //@Test
    public void testGetLiveExchangeRate_Success() {
        // Given
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        String url = "https://example.com/api/latest/USD?apikey=test-api-key";

        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.getRates().put(targetCurrency, new BigDecimal("0.85"));

        // Mocking RestTemplate call
        when(restTemplate.getForObject(url, ExchangeRateResponse.class)).thenReturn(mockResponse);

        // When
        ExchangeRateResponse response = exchangeRateService.getLiveExchangeRate(baseCurrency, targetCurrency);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("0.85"), response.getRates().get(targetCurrency));

        // Verify RestTemplate is called once
        verify(restTemplate, times(1)).getForObject(url, ExchangeRateResponse.class);
    }

    //@Test
    public void testRemoveLiveExchangeRateForCurrency() {
        // This test doesn't require much logic as the method is void and uses the @CacheEvict annotation
        // Simply verify that the method is invoked
        String baseCurrency = "USD";

        // When
        exchangeRateService.removeLiveExchangeRateForCurrency(baseCurrency);

        // Then
        // Verify that the method is called once (no internal logic to test)
        verify(exchangeRateService, times(1)).removeLiveExchangeRateForCurrency(baseCurrency);
    }
}

