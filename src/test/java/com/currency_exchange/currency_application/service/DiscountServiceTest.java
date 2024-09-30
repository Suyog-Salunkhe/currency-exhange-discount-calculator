package com.currency_exchange.currency_application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.currency_exchange.currency_application.model.BillingDetails;
import com.currency_exchange.currency_application.model.Item;
import com.currency_exchange.currency_application.model.UserType;
import com.currency_exchange.currency_application.repository.ItemRepository;
import com.currency_exchange.currency_application.request.BillRequest;
import com.currency_exchange.currency_application.security.CustomerDetails;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {

    @InjectMocks
    private DiscountService discountService;
    
    @Mock
    private ItemRepository itemRepository;
    
    @Test
    public void testApplyDiscounts() {
    	BillRequest billRequest = getMockBillRequest();
    	CustomerDetails customerDetails = getCustomerDetails(UserType.EMPLOYEE, 2);
    	when(itemRepository.findByName(anyList())).thenReturn(getItems());
        BigDecimal discountedTotal = discountService.applyDiscounts(billRequest.getBillingDetails(), customerDetails);
        assertEquals(new BigDecimal(192), discountedTotal);
    }
    
    private CustomerDetails getCustomerDetails(String type, int tenure) {
    	CustomerDetails user = new CustomerDetails("name", "password", type, tenure);
		return user;
	}

	@Test
    public void testApplyDiscountsAffiliate() {
    	
    	BillRequest billRequest = getMockBillRequest();
    	CustomerDetails customerDetails = getCustomerDetails(UserType.AFFILIATE, 2);
    	when(itemRepository.findByName(anyList())).thenReturn(getItems());
        BigDecimal discountedTotal = discountService.applyDiscounts(billRequest.getBillingDetails(), customerDetails);
        
        assertEquals(new BigDecimal(209), discountedTotal);
    }
    
    @Test
    public void testApplyDiscountsCustomer() {
    	
    	BillRequest billRequest = getMockBillRequest();
    	CustomerDetails customerDetails = getCustomerDetails(UserType.CUSTOMER, 3);
    	when(itemRepository.findByName(anyList())).thenReturn(getItems());
        BigDecimal discountedTotal = discountService.applyDiscounts(billRequest.getBillingDetails(), customerDetails);
        
        assertEquals(new BigDecimal(214.5), discountedTotal);
    }
    
    private BillRequest getMockBillRequest() {

		BillingDetails billingDetails = new BillingDetails(getItems(), "INR", "USD");
	
		BillRequest billRequest = new BillRequest();
		billRequest.setBillingDetails(billingDetails);
		
		return billRequest;
	}
    
    private List<Item> getItems(){
    	List<Item> items = new ArrayList<>();

		Item item = new Item("A", 120, true);
		items.add(item);
		item = new Item("B", 110, false);
		items.add(item);

		return items;
    }
}