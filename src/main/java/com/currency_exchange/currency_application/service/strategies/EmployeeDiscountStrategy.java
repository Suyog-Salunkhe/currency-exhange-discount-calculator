package com.currency_exchange.currency_application.service.strategies;

public class EmployeeDiscountStrategy implements DiscountStrategy {
	@Override
	public double applyDiscount(double amount) {
		return amount * 0.70; // 30% discount
	}
}