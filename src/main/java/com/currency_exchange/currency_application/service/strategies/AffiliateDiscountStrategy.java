package com.currency_exchange.currency_application.service.strategies;

public class AffiliateDiscountStrategy implements DiscountStrategy {

	@Override
	public double applyDiscount(double amount) {
		System.out.println(amount);
		return amount * 0.90; // 10% discount
	}
}