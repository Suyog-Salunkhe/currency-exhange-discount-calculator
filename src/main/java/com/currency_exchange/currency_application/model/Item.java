package com.currency_exchange.currency_application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

	private String itemName;
	private double amount;
	private boolean isGrocery;
}