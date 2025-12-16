package com.jadebloom.goblin_api.expense.controller;

import java.time.ZonedDateTime;

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

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ExpenseCategoryControllerIntegrationTests {

	private final MockMvc mockMvc;

	private final ExpenseCategoryService expenseCategoryService;

	private final ObjectMapper objectMapper;

	@Autowired
	public ExpenseCategoryControllerIntegrationTests(
			MockMvc mockMvc,
			@Qualifier("expenseCategoryServiceImpl") ExpenseCategoryService expenseCategoryService) {
		this.mockMvc = mockMvc;

		this.expenseCategoryService = expenseCategoryService;

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
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isString());
	}

	@Test
	public void canReturnHttp400WhenCreatingExpenseCategoryWithInvalidName() throws Exception {
		CreateExpenseCategoryDto dto = new CreateExpenseCategoryDto(null);
		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenCreatingExpenseCategoryWithInvalidDescription() throws Exception {
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
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/" + created.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(created.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(created.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isString());
	}

	@Test
	public void canReturnHttp404WhenFindingNonExistingExpenseCategoryById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnExpenseCategoryAndHttp200WhenUpdatingExpenseCategory() throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Food");
		ExpenseCategoryDto created = expenseCategoryService.create(createDto);

		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(created.getId(), ".");

		String json = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updateDto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateDto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isString());
	}

	@Test
	public void canReturnHttp400WhenUpdatingExpenseCategoryWithInvalidName() throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Food");
		ExpenseCategoryDto dto = expenseCategoryService.create(createDto);

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
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Food");
		ExpenseCategoryDto dto = expenseCategoryService.create(createDto);

		dto.setDescription("");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp404WhenUpdatingNotExistingExpenseCategory() throws Exception {
		ExpenseCategoryDto dto = new ExpenseCategoryDto(1L, "name", ZonedDateTime.now());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/expenses/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnHttp204WhenDeletingExpenseCategoryById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
