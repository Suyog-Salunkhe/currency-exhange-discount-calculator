package com.currency_exchange.currency_application.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.currency_exchange.currency_application.model.Item;

@Repository
public interface ItemRepository {

	List<Item> findByName(List<String> itemNames);
	
}
