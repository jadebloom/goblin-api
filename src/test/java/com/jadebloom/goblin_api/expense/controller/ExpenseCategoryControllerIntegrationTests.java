package com.jadebloom.goblin_api.expense.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ExpenseCategoryControllerIntegrationTests {

	private final MockMvc mockMvc;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final ObjectMapper objectMapper;

	@Autowired
	public ExpenseCategoryControllerIntegrationTests(
			MockMvc mockMvc,
			ExpenseCategoryRepository expenseCategoryRepository) {
		this.mockMvc = mockMvc;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.objectMapper = new ObjectMapper();
	}

	@Test
	public void canReturnExpenseCategoryAndHttp201WhenCreatingExpenseCategory() throws Exception {
		CreateExpenseCategoryDto dto = new CreateExpenseCategoryDto("Daily");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()));
	}

	@Test
	public void canReturnHttp400WhenCreatingInvalidExpenseCategory() throws Exception {
		CreateExpenseCategoryDto dto = new CreateExpenseCategoryDto(null, "");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp200WhenFindingExpenseCategories() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void canReturnExpenseCategoryAndHttp200WhenFindingExistingExpenseCategoryById() throws Exception {
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");

		ExpenseCategoryEntity savedEntity = expenseCategoryRepository.save(entity);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/" + savedEntity.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedEntity.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedEntity.getName()));
	}

	@Test
	public void canReturnHttp404WhenFindingNonExistingExpenseCategoryById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp200WhenUpdatingExistingExpenseCategory() throws Exception {
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedEntity = expenseCategoryRepository.save(entity);

		ExpenseCategoryDto dto = new ExpenseCategoryDto("Debt");
		dto.setId(savedEntity.getId());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()));
	}

	@Test
	public void canReturnHttp400WhenUpdatingExpenseCategoryWithInvalidName() throws Exception {
		ExpenseCategoryEntity entity = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedEntity = expenseCategoryRepository.save(entity);

		ExpenseCategoryDto dto = new ExpenseCategoryDto("  ");
		dto.setId(savedEntity.getId());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp404WhenFullUpdatingNotExistingExpenseCategory() throws Exception {
		ExpenseCategoryDto dto = new ExpenseCategoryDto("Daily");
		dto.setId(1L);

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp204WhenDeletingAllExpenseCategories() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/expenses/categories/all"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void canReturnHttp204WhenDeletingExpenseCategoryById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
