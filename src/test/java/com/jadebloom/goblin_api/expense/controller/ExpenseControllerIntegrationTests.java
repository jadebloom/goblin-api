package com.jadebloom.goblin_api.expense.controller;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.expense.service.ExpenseService;

import jakarta.annotation.PostConstruct;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ExpenseControllerIntegrationTests {

	private static Long expenseCategoryId;

	private static Long currencyId;

	private final MockMvc mockMvc;

	private final ExpenseService expenseService;

	private final ExpenseCategoryService expenseCategoryService;

	private final CurrencyService currencyService;

	private final ObjectMapper objectMapper;

	@Autowired
	public ExpenseControllerIntegrationTests(
			MockMvc mockMvc,
			@Qualifier("expenseServiceImpl") ExpenseService expenseService,
			@Qualifier("expenseCategoryServiceImpl") ExpenseCategoryService expenseCategoryService,
			@Qualifier("currencyServiceImpl") CurrencyService currencyService) {
		this.mockMvc = mockMvc;

		this.expenseService = expenseService;

		this.expenseCategoryService = expenseCategoryService;

		this.currencyService = currencyService;

		this.objectMapper = new ObjectMapper();
	}

	@PostConstruct
	private void createExpenseDependencies() {
		CreateExpenseCategoryDto dto1 = new CreateExpenseCategoryDto("Daily");
		CreateCurrencyDto dto2 = new CreateCurrencyDto("Dollar");

		expenseCategoryId = expenseCategoryService.create(dto1).getId();
		currencyId = currencyService.create(dto2).getId();
	}

	@Test
	public void canReturnExpenseAndHttp201WhenCreatingExpense() throws Exception {
		CreateExpenseDto dto = new CreateExpenseDto("Uber Ride", 100, expenseCategoryId, currencyId);
		dto.setDescription("Descr");
		dto.setLabels(List.of("l1", "l2"));

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.labels")
								.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id")
						.value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()));
	}

	@Test
	public void canReturnHttp404WhenCreatingInvalidExpense() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(null, null, expenseCategoryId, currencyId);

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp404WhenCreatingExpenseWithNonExistingExpenseCategory() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto("Uber Ride", 100, 2L, currencyId);

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp404WhenCreatingExpenseWithNonExistingCurrencyCategory() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto("Uber Ride", 100, expenseCategoryId, 2L);

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp200WhenFindingExpenses() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void canReturnExpenseAndHttp200WhenFindingExpenseById() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto("Uber Ride", 100,
				expenseCategoryId, currencyId);
		createDto.setLabels(List.of("123", "32"));

		ExpenseDto dto = expenseService.create(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/" + dto.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.labels")
								.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id")
						.value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()));
	}

	@Test
	public void canReturnExpenseAndHttp200WhenUpdatingExpense() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100,
				expenseCategoryId,
				currencyId);

		ExpenseDto dto = expenseService.create(createDto);
		dto.setName("New Name");
		dto.setLabels(List.of("NewLabel"));

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.labels")
								.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id")
						.value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()));
	}

}
