package com.currency_exchange.currency_application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.currency_exchange.currency_application.model.BillingDetails;
import com.currency_exchange.currency_application.model.Item;
import com.currency_exchange.currency_application.model.UserType;
import com.currency_exchange.currency_application.request.BillRequest;
import com.currency_exchange.currency_application.response.PayableAmountResponse;
import com.currency_exchange.currency_application.security.CustomUserDetailsService;
import com.currency_exchange.currency_application.security.CustomerDetails;
import com.currency_exchange.currency_application.security.JwtUtils;
import com.currency_exchange.currency_application.service.DiscountService;
import com.currency_exchange.currency_application.service.ExchangeRateService;

public class CurrencyControllerTest {

	@InjectMocks
	private CurrencyController currencyController;

	@Mock
	private ExchangeRateService exchangeRateService;

	@Mock
	private DiscountService discountService;

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private CustomUserDetailsService customerUserDetailsService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCalculatePayableAmount_Success() {
		// Given
		BillRequest billRequest = getMockBillRequest();
		BillingDetails billingDetails = billRequest.getBillingDetails();
		CustomerDetails customerDetails = getCustomerDetails(UserType.EMPLOYEE, 2);
		BigDecimal totalAfterDiscounts = new BigDecimal("80.00"); // Assume discount applied to $100 bill
		BigDecimal exchangeRate = new BigDecimal("0.85"); // Assume 1 USD = 0.85 EUR

		// Mocking the discount and exchange rate services
		when(discountService.applyDiscounts(any(), any())).thenReturn(totalAfterDiscounts);
		when(exchangeRateService.getExchangeRateForCurrency(billingDetails.getOriginalCurrency(),
				billingDetails.getTargetCurrency())).thenReturn(exchangeRate);
		String token = "token";
		// When
		ResponseEntity<PayableAmountResponse> response = currencyController.calculatePayableAmount(billRequest, token);

		// Then
		BigDecimal expectedTotalInTargetCurrency = totalAfterDiscounts.multiply(exchangeRate).setScale(2,
				RoundingMode.UP);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(expectedTotalInTargetCurrency, response.getBody().getPayableAmount().setScale(2));
	}

	@Test
	public void testCalculatePayableAmount_NoDiscount() {
		// Given
		BillRequest billRequest = getMockBillRequest();
		BillingDetails billingDetails = billRequest.getBillingDetails();
		CustomerDetails user = getCustomerDetails("EMPLOYEE", 3);
		BigDecimal totalAfterDiscounts = new BigDecimal(
				billingDetails.getItems().stream().map(i -> i.getAmount())
				.collect(Collectors.summingDouble(Double::doubleValue))); // No discount applied
		BigDecimal exchangeRate = new BigDecimal("1.10"); // Assume 1 USD = 1.10 CAD

		// Mocking the discount and exchange rate services
		when(customerUserDetailsService.getCustomerDetailsByUsername(anyString())).thenReturn(user);
		when(discountService.applyDiscounts(any(), any())).thenReturn(totalAfterDiscounts);
		when(exchangeRateService.getExchangeRateForCurrency(billingDetails.getOriginalCurrency(),
				billingDetails.getTargetCurrency())).thenReturn(exchangeRate);
		String token = "token";
		// When
		ResponseEntity<PayableAmountResponse> response = currencyController.calculatePayableAmount(billRequest, token);
		System.out.println(totalAfterDiscounts);
		// Then
		BigDecimal expectedTotalInTargetCurrency = totalAfterDiscounts.multiply(exchangeRate).setScale(2,RoundingMode.UP);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(expectedTotalInTargetCurrency, response.getBody().getPayableAmount());		
	}
	
	@Test
	public void testCalculatePayableAmount_ExchangeRateFailure() {
		// Given
		BillRequest billRequest = getMockBillRequest();
		BillingDetails billingDetails = billRequest.getBillingDetails();
		CustomerDetails user = getCustomerDetails("EMPLOYEE", 3);
		BigDecimal totalAfterDiscounts = new BigDecimal("70.00"); // Discounted

		// Mocking the discount service and simulating exchange rate failure
		when(customerUserDetailsService.getCustomerDetailsByUsername(anyString())).thenReturn(user);
		when(discountService.applyDiscounts(any(), any())).thenReturn(totalAfterDiscounts);
		when(exchangeRateService.getExchangeRateForCurrency(billingDetails.getOriginalCurrency(),
				billingDetails.getTargetCurrency())).thenThrow(new RuntimeException("Exchange rate service failed"));
		String token = "token";
		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			currencyController.calculatePayableAmount(billRequest, anyString());
		});

		assertEquals("Exchange rate service failed", exception.getMessage());
	}

	
	
	private BillRequest getMockBillRequest() {

		List<Item> items = new ArrayList<>();

		Item item = new Item("A", 120, true);
		items.add(item);
		item = new Item("B", 110, false);
		items.add(item);

		BillingDetails billingDetails = new BillingDetails(items, "INR", "USD");
		BillRequest billRequest = new BillRequest();
		billRequest.setBillingDetails(billingDetails);

		return billRequest;
	}

	private CustomerDetails getCustomerDetails(String type, int tenure) {
		CustomerDetails user = new CustomerDetails("name", "password", type, tenure);
		return user;
	}

}