package com.currency_exchange.currency_application.request;

import com.currency_exchange.currency_application.model.BillingDetails;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillRequest {

	@Valid
	private BillingDetails billingDetails;
	
}