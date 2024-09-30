package com.currency_exchange.currency_application.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetails {
	
	private String name;
    private String password;
    private String userType;
    private int tenureInYears;
}