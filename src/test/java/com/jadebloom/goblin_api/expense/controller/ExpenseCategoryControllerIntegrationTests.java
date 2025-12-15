package com.jadebloom.goblin_api.expense.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
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
	public void canCreateAndReturnExpenseCategoryAndHttp201() throws Exception {
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
	public void canReturnHttp400WhenExpenseCategoryHasInvalidName() throws Exception {
		CreateExpenseCategoryDto dto = new CreateExpenseCategoryDto(null);
		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenExpenseCategoryHasInvalidDescription() throws Exception {
		CreateExpenseCategoryDto dto = new CreateExpenseCategoryDto("n");
		dto.setDescription("");
		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp200WhenFindingExpenseCategories() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void canReturnExpenseCategoryAndHttp200WhenFindingExpenseCategoryById() throws Exception {
		ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedE = expenseCategoryRepository.save(e);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/" + savedE.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedE.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedE.getName()));
	}

	@Test
	public void canReturnHttp404WhenFindingNonExistingExpenseCategoryById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnExpenseCategoryAndHttp200WhenUpdatingExpenseCategory() throws Exception {
		ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedE = expenseCategoryRepository.save(e);

		ExpenseCategoryDto dto = new ExpenseCategoryDto("Debt");
		dto.setId(savedE.getId());
		dto.setName("NewName");

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
		ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedE = expenseCategoryRepository.save(e);

		ExpenseCategoryDto dto = new ExpenseCategoryDto("  ");
		dto.setId(savedE.getId());
		dto.setName(null);

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenUpdatingExpenseCategoryWithInvalidDescription() throws Exception {
		ExpenseCategoryEntity e = new ExpenseCategoryEntity("Daily");
		ExpenseCategoryEntity savedE = expenseCategoryRepository.save(e);

		ExpenseCategoryDto dto = new ExpenseCategoryDto("  ");
		dto.setId(savedE.getId());
		dto.setDescription("  ");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp404WhenUpdatingNotExistingExpenseCategory() throws Exception {
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
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/all"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void canReturnHttp204WhenDeletingExpenseCategoryById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
