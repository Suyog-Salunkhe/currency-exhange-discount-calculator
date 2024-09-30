package com.currency_exchange.currency_application.service.factories;

import com.currency_exchange.currency_application.security.CustomerDetails;
import com.currency_exchange.currency_application.service.strategies.AffiliateDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.DiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.EmployeeDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.NoDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.UserDiscountStrategy;


public class DiscountStrategyFactory {

	public DiscountStrategy getDiscountStrategy(CustomerDetails user) {
		System.out.println(user.toString());
		switch (user.getUserType()) {
		case "EMPLOYEE":
			return new EmployeeDiscountStrategy();
		case "AFFILIATE":
			return new AffiliateDiscountStrategy();
		case "CUSTOMER":
			if(user.getTenureInYears() > 2)
				return new UserDiscountStrategy();
			else 
				return new NoDiscountStrategy();
		}
		return null;
	}
	/*
	 * private String findUserType(User user) {
	 * 
	 * if (user.getType().equalsIgnoreCase("employee")) { return "EMPLOYEE"; } else
	 * if (user.getType().equalsIgnoreCase("affiliate")) { return "AFFILIATE"; }
	 * else if (user.getType().equalsIgnoreCase("customer") &&
	 * user.getTenureInYears() > 2) { return "OLD_CUSTOMER"; } return "NO_DISCOUNT";
	 * }
	 */
}
