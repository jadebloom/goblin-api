package com.jadebloom.goblin_api.currency.controller;

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

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CurrencyControllerIntegrationTests {

	private final MockMvc mockMvc;

	private final ObjectMapper objectMapper;

	private final CurrencyRepository currencyRepository;

	@Autowired
	public CurrencyControllerIntegrationTests(MockMvc mockMvc, CurrencyRepository currencyRepository) {
		this.mockMvc = mockMvc;

		objectMapper = new ObjectMapper();

		this.currencyRepository = currencyRepository;
	}

	@Test
	public void canCreateAndReturnCurrencyAndHttp201() throws Exception {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Dollar", "USD");

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
	public void canReturnHttp400WhenCurrencyHasInvalidName() throws Exception {
		CreateCurrencyDto dto = new CreateCurrencyDto("   ");

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void canReturnHttp400WhenCurrencyHasInvalidAlphabeticalCode() throws Exception {
		CreateCurrencyDto dto = new CreateCurrencyDto("Dollar", "DO");

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
		CurrencyEntity entity = new CurrencyEntity("Dollar");

		CurrencyEntity savedEntity = currencyRepository.save(entity);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/" + savedEntity.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedEntity.getName()));
	}

	@Test
	public void canReturnHttp404WhenFindingNonExistingById() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/currencies/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void canReturnCurrencyAndHttp200WhenUpdatingCurrency() throws Exception {
		CurrencyEntity entity = new CurrencyEntity("Dollar");
		CurrencyEntity savedEntity = currencyRepository.save(entity);

		CurrencyDto dto = new CurrencyDto("Ruble");
		dto.setId(savedEntity.getId());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/v1/currencies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dto.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(dto.getName()));
	}

	@Test
	public void canReturnHttp400WhenUpdatingCurrencyWithInvalidName() throws Exception {
		CurrencyEntity entity = new CurrencyEntity("Valid name");
		CurrencyEntity savedEntity = currencyRepository.save(entity);

		CurrencyDto dto = new CurrencyDto("  ");
		dto.setId(savedEntity.getId());

		String json = objectMapper.writeValueAsString(dto);

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
	public void canReturnHttp204WhenDeletingAllCurrencies() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/all"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void canReturnHttp204WhenDeletingExistingCurrencyByItsId() throws Exception {
		CurrencyEntity entity = new CurrencyEntity("Dollar");

		Long id = currencyRepository.save(entity).getId();

		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/" + id))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void canReturnHttp204WhenDeletingNonExistingCurrencyByItsId() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/currencies/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

}
