package com.jadebloom.goblin_api.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jadebloom.goblin_api.expense.dto.CreateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryInUseException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNameUnavailableException;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.service.ExpenseCategoryService;
import com.jadebloom.goblin_api.expense.service.ExpenseService;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.shared.service.HttpResponseService;
import com.jadebloom.goblin_api.test.MethodSecurityTestConfig;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ExpenseCategoryController.class)
@Import(MethodSecurityTestConfig.class)
public class ExpenseCategoryControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ExpenseCategoryService expenseCategoryService;

	@MockitoBean
	private ExpenseService expenseService;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private HttpResponseService httpResponseService;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void createObjectMapper() {
		objectMapper = new ObjectMapper();
	}

	@Test
	@DisplayName("Return HTTP 201 and expense category when creating expense category with only required fields")
	@WithMockUser(roles = { "USER" })
	public void GivenOnlyRequiredFields_WhenCreating_ThenReturnHttp201AndCreatedExpenseCategory()
			throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		ExpenseCategoryDto dto = new ExpenseCategoryDto(
				1L,
				createDto.getName(),
				ZonedDateTime.now(),
				1L);

		when(expenseCategoryService.create(any(CreateExpenseCategoryDto.class))).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.hex_color_code")
						.value(dto.getHexColorCode()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when creating an expense category with an invalid name")
	@WithMockUser(roles = { "USER" })
	public void GivenInvalidName_WhenCreating_ThenReturnHttp400() throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("   ");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 400 when creating an expense category with an unavailable name")
	@WithMockUser(roles = { "USER" })
	public void GivenUnavailableName_WhenCreating_ThenReturnHttp400() throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		when(expenseCategoryService.create(any(CreateExpenseCategoryDto.class)))
				.thenThrow(ExpenseCategoryNameUnavailableException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when creating new expense category as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenCreatingExpenseCategory_ThenReturnHttp403()
			throws Exception {
		CreateExpenseCategoryDto createDto = new CreateExpenseCategoryDto("Daily");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 when finding the authenticated user expense categories")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseCategories_WhenFindingAuthenticatedUserExpenseCategories_ThenReturnHttp200()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Return HTTP 403 when finding the authenticated user expense categories without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenFindingAuthenticatedUserExpenseCategories_ThenReturnHttp403()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 and the expense category when finding it by its ID")
	@WithMockUser(roles = "USER")
	public void GivenExpenseCategory_WhenFindingItById_ThenReturnHttp200AndExpenseCategory()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Return HTTP 403 when finding an expense category by ID without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenFindingById_ThenReturnHttp403()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when finding a non-existing expense category by ID")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenFindingById_ThenReturnHttp404()
			throws Exception {
		when(expenseCategoryService.findById(anyLong()))
				.thenThrow(ExpenseCategoryNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 200 and expense category when updating")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseCategory_WhenUpdating_ThenReturnHttp200AndExpenseCategory()
			throws Exception {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(1L, "Daily");

		ExpenseCategoryDto returned = new ExpenseCategoryDto(
				updateDto.getId(),
				updateDto.getName(),
				ZonedDateTime.now(),
				1L);

		when(expenseCategoryService.update(any(UpdateExpenseCategoryDto.class)))
				.thenReturn(returned);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(returned.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(returned.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description")
						.value(returned.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.hex_color_code")
						.value(returned.getHexColorCode()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id")
						.value(returned.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when updating an expense category with an invalid name")
	@WithMockUser(roles = { "USER" })
	public void GivenInvalidName_WhenUpdating_ThenReturnHttp400() throws Exception {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(1L, "   ");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 400 when updating an expense category with an unavailable name")
	@WithMockUser(roles = { "USER" })
	public void GivenUnavailableName_WhenUpdating_ThenReturnHttp400() throws Exception {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(1L, "Daily");

		when(expenseCategoryService.update(any(UpdateExpenseCategoryDto.class)))
				.thenThrow(ExpenseCategoryNameUnavailableException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when updating an expense category as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenUpdating_ThenReturnHttp403() throws Exception {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(1L, "Daily");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when updating a non-existing expense category")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenUpdatingIt_ThenReturnHttp400()
			throws Exception {
		UpdateExpenseCategoryDto updateDto = new UpdateExpenseCategoryDto(1L, "Daily");

		when(expenseCategoryService.update(any(UpdateExpenseCategoryDto.class)))
				.thenThrow(ExpenseCategoryNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses/categories")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 204 when deleting all possible expenses by their category's ID")
	@WithMockUser(roles = { "USER" })
	public void GivenPossibleExpenses_WhenDeletingAllExpensesByExpenseCategoryId_ThenReturnHttp204()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1/expenses")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to delete all expenses by their category's ID without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenDeletingAllExpensesByExpenseCategoryId_ThenReturnHttp403()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1/expenses")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to delete all possible expenses by a non-existing category's ID")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenDeletingAllExpensesByExpenseCategoryId_ThenReturnHttp404()
			throws Exception {
		doThrow(ExpenseCategoryNotFoundException.class)
				.when(expenseService)
				.deleteAllExpensesByExpenseCategoryId(anyLong());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1/expenses")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 204 when deleting an expense category by its ID")
	@WithMockUser(roles = { "USER" })
	public void GivenExistingExpenseCategory_WhenDeletingById_ThenReturnHttp204() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to delete an expense category by ID without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenDeletingById_ThenReturnHttp403() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to delete a non-existing expense category by ID")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenDeletingById_ThenReturnHttp404()
			throws Exception {
		doThrow(ExpenseCategoryNotFoundException.class)
				.when(expenseCategoryService)
				.deleteById(anyLong());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 409 when trying to delete an expense category that is in use")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseCategoryInUse_WhenDeletingById_ThenReturnHttp409() throws Exception {
		doThrow(ExpenseCategoryInUseException.class)
				.when(expenseCategoryService)
				.deleteById(anyLong());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/categories/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

}
