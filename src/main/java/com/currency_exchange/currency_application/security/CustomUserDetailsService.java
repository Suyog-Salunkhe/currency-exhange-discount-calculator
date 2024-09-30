package com.currency_exchange.currency_application.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private Map<String, CustomerDetails> users = new HashMap<>();

	public CustomUserDetailsService() {

		CustomerDetails customer = new CustomerDetails("employee1", "password123", "EMPLOYEE", 3);
		users.put("employee1", customer);

		CustomerDetails customer2 = new CustomerDetails("employee2", "password1234", "EMPLOYEE", 1);
		users.put("employee2", customer2);

		CustomerDetails customer3 = new CustomerDetails("affiliate", "affiliate1234", "AFFILIATE", 3);
		users.put("affiliate", customer3);

		CustomerDetails customer4 = new CustomerDetails("customer", "customer123", "CUSTOMER", 1);
		users.put("customer", customer4);

		CustomerDetails customer5 = new CustomerDetails("customer2", "customer1234", "CUSTOMER", 3);
		users.put("customer2", customer5);

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (users.containsKey(username)) {
			String password = users.get(username).getPassword();

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(password);
			return User.withUsername(username).password(encodedPassword)
					.build();
		} else {
			throw new UsernameNotFoundException("User not found: " + username);
		}
	}

	public CustomerDetails getCustomerDetailsByUsername(String username) {
		return users.get(username);
	}
}