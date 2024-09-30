package com.currency_exchange.currency_application.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingDetails {
	
	private List<Item> items;
	private String originalCurrency;
	private String targetCurrency;
}
