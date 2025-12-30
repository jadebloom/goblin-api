package com.jadebloom.goblin_api.currency.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyInUseException;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.service.CurrencyService;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.test.MethodSecurityTestConfig;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CurrencyController.class)
@Import({ MethodSecurityTestConfig.class })
public class CurrencyControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CurrencyService currencyService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	@DisplayName("Return HTTP 201 and currency when creating it with required fields")
	@WithMockUser(roles = { "USER" })
	public void GivenRequiredFields_WhenCreatingCurrency_ThenReturnHttp201AndCreatedCurrency()
			throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto currencyDto = new CurrencyDto(1L, createDto.getName(), ZonedDateTime.now(), 1L);

		when(currencyService.create(any(CreateCurrencyDto.class))).thenReturn(currencyDto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(currencyDto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(currencyDto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(currencyDto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when creating new currency with invalid fields")
	@WithMockUser(roles = "USER")
	public void GivenInvalidFields_WhenCreatingCurrency_ThenReturnHttp400() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("  ");
		createDto.setAlphabeticalCode("INVALID");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 400 when creating new currency with unavailable name")
	@WithMockUser(roles = "USER")
	public void GivenUnavailableCurrencyName_WhenCreatingCurrency_ThenReturnHttp400() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		when(currencyService.create(any(CreateCurrencyDto.class)))
				.thenThrow(CurrencyNameUnavailableException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when creating new currency as a user with invalid permissions")
	@WithMockUser(roles = "SOME_ROLE_THAT_IS_NOT_USER")
	public void GivenInvalidUserRole_WhenCreatingCurrency_ThenReturnHttp403() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(createDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 when finding currencies with no query params")
	@WithMockUser(roles = "USER")
	public void GivenNoQueryParameters_WhenFindingAuthenticatedUserCurrencies_ThenReturnHttp200()
			throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Return HTTP 403 when finding currencies as user with invalid permissions")
	@WithMockUser(roles = "SOME_ROLE_THAT_IS_NOT_USER")
	public void GivenInvalidUserRole_WhenFindingAuthenticatedUserCurrencies_ThenReturnHttp403() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 and currency when finding it by its ID")
	@WithMockUser(roles = "USER")
	public void GivenCurrency_WhenFindingItById_ThenReturnHttp200AndThatCurrency() throws Exception {
		CurrencyDto dto = new CurrencyDto(1L, "Tenge", ZonedDateTime.now(), 1L);
		dto.setAlphabeticalCode("KZT");

		when(currencyService.findById(anyLong())).thenReturn(dto);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/currencies/" + dto.getId()).with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.alphabetical_code").value(dto.getAlphabeticalCode()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 404 when finding non-existing currency by its ID")
	@WithMockUser(roles = "USER")
	public void GivenNonExistingCurrency_WhenFindingCurrencyById_ThenReturnHttp404() throws Exception {
		when(currencyService.findById(anyLong())).thenThrow(CurrencyNotFoundException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/1").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 403 when finding currency by ID as a user with an invalid role")
	@WithMockUser(roles = "SOME_ROLE_THAT_IS_NOT_USER")
	public void GivenUserWithInvalidRole_WhenFindingCurrencyById_ThenReturnHttp403() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/1").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 200 and updated currency when updating it with required fields")
	@WithMockUser(roles = "USER")
	public void GivenRequiredFields_WhenUpdatingCurrency_ThenReturnHttp200AndUpdatedCurrency()
			throws Exception {
		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(1L, "New Tenge");
		CurrencyDto dto = new CurrencyDto(
				updateDto.getId(),
				updateDto.getName(),
				ZonedDateTime.now(),
				1L);

		when(currencyService.update(any(UpdateCurrencyDto.class))).thenReturn(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.creator_id").value(dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return HTTP 400 when updating a currency using invalid fields")
	@WithMockUser(roles = "USER")
	public void GivenInvalidFields_WhenUpdatingCurrency_ThenReturnHttp400()
			throws Exception {
		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(1L, "    ");

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 400 when updating a currency using unavailable name")
	@WithMockUser(roles = "USER")
	public void GivenUnavailableCurrencyName_WhenUpdatingCurrency_ThenReturnHttp400()
			throws Exception {
		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(1L, "Tenge");

		when(currencyService.update(any(UpdateCurrencyDto.class)))
				.thenThrow(CurrencyNameUnavailableException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Return HTTP 403 when updating a currency as a user with an invalid role")
	@WithMockUser(roles = "SOME_ROLE_THAT_IS_NOT_USER")
	public void GivenInvalidUserRole_WhenUpdatingCurrency_ThenReturnHttp403() throws Exception {
		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(1L, "New Tenge");

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 404 when updating a currency by ID using a non-existing ID")
	@WithMockUser(roles = "USER")
	public void GivenNonExistingCurrencyId_WhenUpdatingCurrency_ThenReturnHttp404()
			throws Exception {
		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(1L, "New Tenge");

		when(currencyService.update(any(UpdateCurrencyDto.class)))
				.thenThrow(CurrencyNotFoundException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(updateDto)))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DisplayName("Return HTTP 204 when deleting a currency with a non-existing ID")
	@WithMockUser(roles = "USER")
	public void GivenNonExistingCurrencyId_WhenDeletingCurrencyById_ThenReturnHttp204()
			throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/1").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Return HTTP 403 when deleting a currency by ID as a user with an invalid role")
	@WithMockUser(roles = "SOME_ROLE_THAT_IS_NOT_USER")
	public void GivenInvalidUserRole_WhenDeletingCurrencyById_ThenReturnHttp403()
			throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/1").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 409 when deleting a currency that is in use")
	@WithMockUser(roles = "USER")
	public void GivenCurrencyInUse_WhenDeletingItById_ThenReturnHttp409()
			throws Exception {
		doThrow(CurrencyInUseException.class).when(currencyService).deleteById(anyLong());

		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/1").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

}
