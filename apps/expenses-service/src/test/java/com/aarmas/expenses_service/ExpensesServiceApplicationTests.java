package com.aarmas.expenses_service;

import com.aarmas.expenses_service.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ExpensesServiceApplicationTests {

	@MockitoBean
	ExpenseRepository expenseRepository;

	@Test
	void contextLoads() {
	}

}
