package com.currency_exchange.currency_application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currency_exchange.currency_application.model.BillingDetails;
import com.currency_exchange.currency_application.model.Item;
import com.currency_exchange.currency_application.repository.ItemRepository;
import com.currency_exchange.currency_application.security.CustomerDetails;
import com.currency_exchange.currency_application.service.factories.DiscountStrategyFactory;
import com.currency_exchange.currency_application.service.strategies.DiscountStrategy;

@Service
public class DiscountService {

	private DiscountStrategyFactory discountStrategyFactory;

	@Autowired
	private ItemRepository itemRepository;

	public BigDecimal applyDiscounts(BillingDetails billingDetails, CustomerDetails user) {

		discountStrategyFactory = new DiscountStrategyFactory();

		DiscountStrategy discountStrategy = discountStrategyFactory.getDiscountStrategy(user);

		List<Item> items = itemRepository
				.findByName(billingDetails.getItems().stream().map(e -> e.getItemName()).collect(Collectors.toList()));

		// Apply percentage-based discounts
		Double nonGrocery = items.stream()
				.filter(item -> !item.isGrocery())
				.peek(i -> i.setAmount(discountStrategy.applyDiscount(i.getAmount())))
				.map(item -> item.getAmount())
				.collect(Collectors.summingDouble(Double::doubleValue));

		Double grocery = items.stream()
				.filter(item -> item.isGrocery())
				.map(item -> item.getAmount())
				.collect(Collectors.summingDouble(Double::doubleValue));
		
		BigDecimal total = new BigDecimal(nonGrocery + grocery);
		
		// $5 discount for every $100 spent
		total = applyFixedDiscount(total);

		return total;
	}

	private BigDecimal applyFixedDiscount(BigDecimal amount) {
		int hundredDollarUnits = amount.divide(BigDecimal.valueOf(100)).intValue();
		return amount.subtract(BigDecimal.valueOf(hundredDollarUnits * 5));
	}
}
