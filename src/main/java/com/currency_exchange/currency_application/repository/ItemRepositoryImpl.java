package com.currency_exchange.currency_application.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.currency_exchange.currency_application.model.Item;

@Service
public class ItemRepositoryImpl implements ItemRepository {

	public static List<Item> getItemsList() {
		List<Item> list = new ArrayList<Item>();

		Item item = new Item("A", 120, true);
		list.add(item);

		item = new Item("B", 100, false);
		list.add(item);

		item = new Item("C", 150, true);
		list.add(item);

		item = new Item("D", 200, false);
		list.add(item);
		return list;
	}

	@Override
	public List<Item> findByName(List<String> itemNames) {
		List<Item> list = getItemsList();
		System.out.println(itemNames);
		return list.stream().filter(e -> itemNames.contains(e.getItemName())).collect(Collectors.toList());
	}

}
