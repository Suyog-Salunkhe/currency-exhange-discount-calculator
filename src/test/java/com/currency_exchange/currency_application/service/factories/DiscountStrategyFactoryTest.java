package com.currency_exchange.currency_application.service.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.currency_exchange.currency_application.security.CustomerDetails;
import com.currency_exchange.currency_application.service.strategies.AffiliateDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.DiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.EmployeeDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.NoDiscountStrategy;
import com.currency_exchange.currency_application.service.strategies.UserDiscountStrategy;

public class DiscountStrategyFactoryTest {

	@Test
	public void testGetDiscountStrategyAffiliate() {
		CustomerDetails customer = new CustomerDetails("affiliate", "affiliate1234", "AFFILIATE", 3);

		DiscountStrategyFactory factory = new DiscountStrategyFactory();
		DiscountStrategy strategy = factory.getDiscountStrategy(customer);

		assertEquals(AffiliateDiscountStrategy.class, strategy.getClass());
	}

	@Test
	public void testGetDiscountStrategyEmployee() {
		CustomerDetails customer = new CustomerDetails("employee1", "password123", "EMPLOYEE", 3);

		DiscountStrategyFactory factory = new DiscountStrategyFactory();
		DiscountStrategy strategy = factory.getDiscountStrategy(customer);

		assertEquals(EmployeeDiscountStrategy.class, strategy.getClass());
	}

	@Test
	public void testGetDiscountStrategyCustomer() {
		CustomerDetails customer = new CustomerDetails("customer2", "customer1234", "CUSTOMER", 3);

		DiscountStrategyFactory factory = new DiscountStrategyFactory();
		DiscountStrategy strategy = factory.getDiscountStrategy(customer);

		assertEquals(UserDiscountStrategy.class, strategy.getClass());
	}

	@Test
	public void testGetDiscountStrategyNoDiscount() {
		CustomerDetails customer = new CustomerDetails("customer", "customer123", "CUSTOMER", 1);

		DiscountStrategyFactory factory = new DiscountStrategyFactory();
		DiscountStrategy strategy = factory.getDiscountStrategy(customer);

		assertEquals(NoDiscountStrategy.class, strategy.getClass());
	}

}
