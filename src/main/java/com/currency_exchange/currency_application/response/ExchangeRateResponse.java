package com.currency_exchange.currency_application.response;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class ExchangeRateResponse {
    private String base_code;
    private Long time_next_update_unix;
    private Map<String, BigDecimal> rates;
}