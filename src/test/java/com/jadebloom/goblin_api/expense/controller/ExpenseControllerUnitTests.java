package com.jadebloom.goblin_api.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.ZonedDateTime;
import java.util.List;

import org.hamcrest.Matchers;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.expense.dto.CreateExpenseDto;
import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.dto.UpdateExpenseDto;
import com.jadebloom.goblin_api.expense.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.service.ExpenseService;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.test.MethodSecurityTestConfig;

@WebMvcTest(ExpenseController.class)
@Import(MethodSecurityTestConfig.class)
public class ExpenseControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ExpenseService expenseService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	@DisplayName("Return HTTP 201 and expense when creating it with all fields")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseWithAllFields_WhenCreating_ThenReturnHttp201AndCreatedExpense()
			throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				1L,
				1L);
		createDto.setDescription("Magnificent ride");
		createDto.setLabels(List.of("Label1", "Label2"));

		ExpenseDto dto = new ExpenseDto(
				1L,
				createDto.getName(),
				createDto.getAmount(),
				ZonedDateTime.now(),
				createDto.getExpenseCategoryId(),
				createDto.getCurrencyId(),
				1L);
		dto.setDescription(createDto.getDescription());
		dto.setLabels(createDto.getLabels());

		when(expenseService.create(any(CreateExpenseDto.class))).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.labels")
						.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id").value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 201 and expense when creating it with required fields")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseWithRequiredFields_WhenCreating_ThenReturnHttp201AndCreatedExpense()
			throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				1L,
				1L);

		ExpenseDto dto = new ExpenseDto(
				1L,
				createDto.getName(),
				createDto.getAmount(),
				ZonedDateTime.now(),
				createDto.getExpenseCategoryId(),
				createDto.getCurrencyId(),
				1L);

		when(expenseService.create(any(CreateExpenseDto.class))).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id").value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when trying to create an expense that is invalid")
	@WithMockUser(roles = { "USER" })
	public void GivenInvalidExpense_WhenCreating_ThenReturnHttp400() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"",
				100L,
				1L,
				1L);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to create an expense as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenCreating_ThenReturnHttp403() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				1L,
				1L);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to create an expense with a non-existing category")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenCreating_ThenReturnHttp404() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				1L,
				1L);

		when(expenseService.create(any(CreateExpenseDto.class)))
				.thenThrow(ExpenseCategoryNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to create an expense with a non-existing currency")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingCurrency_WhenCreating_ThenReturnHttp404() throws Exception {
		CreateExpenseDto createDto = new CreateExpenseDto(
				"Uber Ride",
				100L,
				1L,
				1L);

		when(expenseService.create(any(CreateExpenseDto.class)))
				.thenThrow(CurrencyNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 200 when finding any authenticated user expenses")
	@WithMockUser(roles = { "USER" })
	public void GivenAnyNumberOfExpenses_WhenFindingAuthenticatedUserExpenses_ThenReturnHttp200()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Return HTTP 403 when finding any authenticated user expenses as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenFindingAuthenticatedUserExpenses_ThenReturnHttp403()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 and expense when finding existing expense by ID")
	@WithMockUser(roles = { "USER" })
	public void GivenExistingExpense_WhenFindingItById_ThenReturnHttp200AndExpense()
			throws Exception {
		ExpenseDto dto = new ExpenseDto(
				1L,
				"Uber Ride",
				100L,
				ZonedDateTime.now(),
				1L,
				1L,
				1L);
		dto.setDescription("Magnificent ride");
		dto.setLabels(List.of("Label1", "Label2"));

		when(expenseService.findById(anyLong())).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.labels")
						.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id").value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 403 when finding an expense by ID as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenFindingById_ThenReturnHttp403() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to find a non-existing expense by ID")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpense_WhenFindingItById_ThenReturn404() throws Exception {
		when(expenseService.findById(anyLong())).thenThrow(ExpenseNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 200 and expense when updating with all fields")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseWithAllFields_WhenUpdating_ThenReturnHttp200AndExpense()
			throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);
		updateDto.setDescription("Magnificent ride");
		updateDto.setLabels(List.of("Label1", "Label2"));

		ExpenseDto dto = new ExpenseDto(
				updateDto.getId(),
				updateDto.getName(),
				updateDto.getAmount(),
				ZonedDateTime.now(),
				updateDto.getExpenseCategoryId(),
				updateDto.getCurrencyId(),
				1L);
		dto.setDescription(updateDto.getDescription());
		dto.setLabels(updateDto.getLabels());

		when(expenseService.update(any(UpdateExpenseDto.class))).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dto.getDescription()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.labels")
						.value(Matchers.containsInAnyOrder(dto.getLabels().toArray())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id").value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 200 and expense when updating with only required fields")
	@WithMockUser(roles = { "USER" })
	public void GivenExpenseWithOnlyRequiredFields_WhenUpdating_ThenReturnHttp200AndExpense()
			throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);

		ExpenseDto dto = new ExpenseDto(
				updateDto.getId(),
				updateDto.getName(),
				updateDto.getAmount(),
				ZonedDateTime.now(),
				updateDto.getExpenseCategoryId(),
				updateDto.getCurrencyId(),
				1L);

		when(expenseService.update(any(UpdateExpenseDto.class))).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(dto.getAmount()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.expense_category_id").value(dto.getExpenseCategoryId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currency_id").value(dto.getCurrencyId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when updating with an invalid expense")
	@WithMockUser(roles = { "USER" })
	public void GivenInvalidExpense_WhenUpdating_ThenReturnHttp400()
			throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"",
				100L,
				1L,
				1L);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to update as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenUpdating_ThenReturnHttp403() throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to update a non-existing expense")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpense_WhenUpdating_ThenReturnHttp404() throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);

		when(expenseService.update(any(UpdateExpenseDto.class))).thenThrow(ExpenseNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to update with a non-existing expense category")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpenseCategory_WhenUpdating_ThenReturnHttp404() throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);

		when(expenseService.update(any(UpdateExpenseDto.class)))
				.thenThrow(ExpenseCategoryNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to update with a non-existing currency")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingCurrency_WhenUpdating_ThenReturnHttp404() throws Exception {
		UpdateExpenseDto updateDto = new UpdateExpenseDto(
				1L,
				"Uber Ride",
				100L,
				1L,
				1L);

		when(expenseService.update(any(UpdateExpenseDto.class)))
				.thenThrow(CurrencyNotFoundException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/expenses")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 204 when deleting an expense by its ID")
	@WithMockUser(roles = { "USER" })
	public void GivenExistingExpense_WhenDeletingById_ThenReturnHttp204() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to delete an expense as a user without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenDeletingById_ThenReturnHttp403() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when trying to delete a non-existing expense by ID")
	@WithMockUser(roles = { "USER" })
	public void GivenNonExistingExpense_WhenDeletingById_ThenReturnHttp404() throws Exception {
		doThrow(ExpenseNotFoundException.class).when(expenseService).deleteById(anyLong());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/expenses/1")
				.with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
