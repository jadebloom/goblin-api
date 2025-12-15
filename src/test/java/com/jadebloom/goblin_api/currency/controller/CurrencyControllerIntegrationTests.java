package com.jadebloom.goblin_api.currency.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.expense.service.ExpenseService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CurrencyControllerIntegrationTests {

	private final MockMvc mockMvc;

	private final CurrencyService currencyService;

	private final ExpenseCategoryService expenseCategoryService;

	private final ExpenseService expenseService;

	private final ObjectMapper objectMapper;

	@Autowired
	public CurrencyControllerIntegrationTests(
			MockMvc mockMvc,
			@Qualifier("currencyServiceImpl") CurrencyService currencyService,
			ExpenseCategoryService expenseCategoryService,
			ExpenseService expenseService) {
		this.mockMvc = mockMvc;

		this.currencyService = currencyService;

		this.expenseCategoryService = expenseCategoryService;

		this.expenseService = expenseService;

		objectMapper = new ObjectMapper();
	}

	@Test
	public void canCreateAndReturnCurrencyAndHttp201() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Dollar");

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(createDto.getName()));
	}

	@Test
	public void canReturnHttp400WhenCreatingCurrencyWithInvalidName() throws Exception {
		CreateCurrencyDto dto = new CreateCurrencyDto("   ");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenCreatingCurrencyWithInvalidAlphabeticalCode() throws Exception {
		CreateCurrencyDto dto = new CreateCurrencyDto("Dollar");
		dto.setAlphabeticalCode("D");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp200WhenFindingCurrencies() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void canReturnCurrencyAndHttp200WhenFindingCurrencyById() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto currency = currencyService.create(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/" + currency.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(currency.getName()));
	}

	@Test
	public void canReturnHttp404WhenFindingNonExistingCurrencyById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnCurrencyAndHttp200WhenUpdatingCurrency() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto currency = currencyService.create(createDto);
		currency.setName("NeName");

		String json = objectMapper.writeValueAsString(currency);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(currency.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(currency.getName()));
	}

	@Test
	public void canReturnHttp400WhenUpdatingCurrencyWithInvalidName() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto currency = currencyService.create(createDto);
		currency.setName(null);

		String json = objectMapper.writeValueAsString(currency);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenUpdatingCurrencyWithInvalidAlphabeticalCode() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto currency = currencyService.create(createDto);
		currency.setAlphabeticalCode("AB");

		String json = objectMapper.writeValueAsString(currency);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp404WhenUpdatingNotExistingCurrency() throws Exception {
		CurrencyDto dto = new CurrencyDto("Dollar");
		dto.setId(1L);

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp204WhenDeletingCurrencyById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/currencies/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void canReturnHttp409WhenDeletingCurrencyInUseById() throws Exception {
		CreateExpenseCategoryDto createDto1 = new CreateExpenseCategoryDto(".");
		Long expenseCategoryId = expenseCategoryService.create(createDto1).getId();

		CreateCurrencyDto createDto2 = new CreateCurrencyDto(".");
		Long currencyId = currencyService.create(createDto2).getId();

		CreateExpenseDto createDto3 = new CreateExpenseDto(
				".",
				100L,
				expenseCategoryId,
				currencyId);
		expenseService.create(createDto3);

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/currencies/" + currencyId))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

}
