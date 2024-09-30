package com.currency_exchange.currency_application.service.strategies;

public class UserDiscountStrategy implements DiscountStrategy {

	@Override
	public double applyDiscount(double amount) {

		return amount * 0.95; // 5% discount
	}

}