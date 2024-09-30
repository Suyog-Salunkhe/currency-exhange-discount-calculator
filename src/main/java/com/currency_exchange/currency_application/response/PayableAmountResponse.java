package com.currency_exchange.currency_application.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayableAmountResponse {
    private BigDecimal payableAmount;
}