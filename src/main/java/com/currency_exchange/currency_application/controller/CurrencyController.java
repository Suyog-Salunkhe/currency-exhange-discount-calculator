package com.currency_exchange.currency_application.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.currency_exchange.currency_application.model.BillingDetails;
import com.currency_exchange.currency_application.request.BillRequest;
import com.currency_exchange.currency_application.response.PayableAmountResponse;
import com.currency_exchange.currency_application.security.CustomUserDetailsService;
import com.currency_exchange.currency_application.security.CustomerDetails;
import com.currency_exchange.currency_application.security.JwtUtils;
import com.currency_exchange.currency_application.service.DiscountService;
import com.currency_exchange.currency_application.service.ExchangeRateService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api")
public class CurrencyController {

	@Autowired
	private DiscountService discountService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	@Autowired
	private CustomUserDetailsService customerUserDetailsService;

	@Autowired
	private JwtUtils jwtUtil;

	@PostMapping("/calculate")
	public ResponseEntity<PayableAmountResponse> calculatePayableAmount(@RequestBody @Valid BillRequest billRequest,
			@RequestHeader("Authorization") String token) {

		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		String username = jwtUtil.extractUsername(token);

		CustomerDetails customerDetails = customerUserDetailsService.getCustomerDetailsByUsername(username);

		BillingDetails billingDetails = billRequest.getBillingDetails();

		// Apply discounts 
		BigDecimal totalAfterDiscounts = discountService.applyDiscounts(billingDetails, customerDetails);

		// Fetch exchange rate and convert 
		BigDecimal exchangeRate = exchangeRateService.getExchangeRateForCurrency(billingDetails.getOriginalCurrency(),
				billingDetails.getTargetCurrency());
		BigDecimal totalInTargetCurrency = totalAfterDiscounts.multiply(exchangeRate);

		return ResponseEntity.ok(new PayableAmountResponse(totalInTargetCurrency));
	}

}
