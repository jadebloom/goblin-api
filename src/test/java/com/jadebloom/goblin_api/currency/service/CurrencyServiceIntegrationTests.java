package com.jadebloom.goblin_api.currency.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.UpdateCurrencyDto;
import com.jadebloom.goblin_api.currency.error.CurrencyNameUnavailableException;
import com.jadebloom.goblin_api.currency.error.CurrencyNotFoundException;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.UserTestUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@SpringBootTest
@Transactional
public class CurrencyServiceIntegrationTests {

	private final CurrencyService currencyService;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public CurrencyServiceIntegrationTests(
			CurrencyService currencyService,
			UserTestUtils userTestUtils) {
		this.currencyService = currencyService;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createUser() {
		user = userTestUtils.createUserWithPossiblyExistingRoles(
				"user@gmail.com",
				"123",
				Set.of("ROLE_USER"));
	}

	@Test
	@DisplayName("Return currency when creating it with all valid fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidCurrencyWithAllFields_WhenCreating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		createDto.setAlphabeticalCode("KZT");

		CurrencyDto dto = currencyService.create(createDto);

		assertAll("Assert that a valid currency with all fields can be created",
				() -> assertTrue(dto.getId() != null),
				() -> assertEquals(createDto.getName(), dto.getName()),
				() -> assertEquals(createDto.getAlphabeticalCode(), dto.getAlphabeticalCode()),
				() -> assertTrue(dto.getCreatedAt() != null),
				() -> assertEquals(user.getId(), dto.getCreatorId()));
	}

	@Test
	@DisplayName("Return currency when creating it with all required fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidCurrencyWithRequiredFields_WhenCreating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		CurrencyDto dto = currencyService.create(createDto);

		assertAll("Assert that a valid currency with required fields can be created",
				() -> assertTrue(dto.getId() != null),
				() -> assertEquals(createDto.getName(), dto.getName()),
				() -> assertTrue(dto.getAlphabeticalCode() == null),
				() -> assertTrue(dto.getCreatedAt() != null),
				() -> assertEquals(user.getId(), dto.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidCurrencyException when trying to create an invalid currency")
	@WithMockUser(username = "user@gmail.com")
	public void GivenInvalidCurrency_WhenCreating_ThenThrowInvalidCurrencyException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto(" ");

		assertThrowsExactly(InvalidCurrencyException.class, () -> currencyService.create(createDto));
	}

	@Test
	@DisplayName("Throw CurrencyNameUnavailableException when trying to create a currency with an unavailable name")
	@WithMockUser(username = "user@gmail.com")
	public void GivenUnavailableName_WhenCreating_ThenThrowCurrencyNameUnavailableException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		currencyService.create(createDto);

		assertThrowsExactly(CurrencyNameUnavailableException.class, () -> currencyService.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create a currency without having the authenticated user email")
	public void GivenWithoutAuthenticatedUserEmail_WhenCreating_ThenThrowForbiddenException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		assertThrowsExactly(ForbiddenException.class, () -> currencyService.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create a currency without as an authenticated user that doesn't exist")
	@WithMockUser(username = "baduser@gmail.com")
	public void GivenWithoutExistingAuthenticatedUser_WhenCreating_ThenThrowForbiddenException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		assertThrowsExactly(ForbiddenException.class, () -> currencyService.create(createDto));
	}

	@Test
	@DisplayName("Return a page when finding the authenticated user currencies, regardless if they exist or not")
	@WithMockUser(username = "user@gmail.com")
	public void GivenAnyNumberOfCurrencies_WhenFindingAuthenticatedUserCurrencies_ThenReturnPage() {
		Page<CurrencyDto> page = currencyService.findAuthenticatedUserCurrencies(
				PageRequest.of(0, 20));

		assertAll(
				"Assert that a page is returned when finding the authenticated user currencies, regardless if they exist or not",
				() -> assertNotNull(page),
				() -> assertNotNull(page.getContent()),
				() -> assertEquals(0, page.getContent().size()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find the authenticated user currencies without its email")
	public void GivenWithoutAuthenticatedUserEmail_WhenFindingAuthenticatedUserCurrencies_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> currencyService.findAuthenticatedUserCurrencies(PageRequest.of(0, 20)));
	}

	@Test
	@DisplayName("Return a currency when finding it by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenExistingCurrency_WhenFindingItById_ThenReturnIt() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		createDto.setAlphabeticalCode("KZT");
		CurrencyDto dto = currencyService.create(createDto);

		CurrencyDto found = currencyService.findById(dto.getId());

		assertAll("Assert that an existing currency can be found by its ID",
				() -> assertEquals(dto.getId(), found.getId()),
				() -> assertEquals(dto.getName(), found.getName()),
				() -> assertEquals(dto.getAlphabeticalCode(), found.getAlphabeticalCode()),
				() -> assertNotNull(found.getCreatedAt()),
				() -> assertEquals(dto.getCreatorId(), found.getCreatorId()));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to find a non-existing currency by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingCurrency_WhenFindingById_ThenThrowCurrencyNotFoundException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto dto = currencyService.create(createDto);

		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> currencyService.findById(dto.getId() + 1));
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenExistingCurrency_WhenCheckingItsExistenceById_ThenReturnTrue() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		boolean isExists = currencyService.existsById(created.getId());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return false when checking the existence of a non-existing currency by its ID")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingCurrency_WhenCheckingItsExistenceById_ThenReturnFalse() {
		boolean isExists = currencyService.existsById(1L);

		assertFalse(isExists);
	}

	@Test
	@DisplayName("Throw ForbiddenException when checking the existence of a currency by ID")
	public void GivenWithoutAuthenticatedUserEmail_WhenCheckingExistenceById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> currencyService.existsById(1L));
	}

	@Test
	@DisplayName("Return a currency when updating using all fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidCurrencyWithAllFields_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getId(), "Dollar");
		updateDto.setAlphabeticalCode("KZT");
		CurrencyDto updated = currencyService.update(updateDto);

		assertAll("Assert that a currency can be updated using all fields",
				() -> assertEquals(updateDto.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Return a currency when updating using only required fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenValidCurrencyWithOnlyRequiredFields_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getId(), "Dollar");
		CurrencyDto updated = currencyService.update(updateDto);

		assertAll("Assert that a currency can be updated using all fields",
				() -> assertEquals(updateDto.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(created.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Return a currency when updating using the same name")
	@WithMockUser(username = "user@gmail.com")
	public void GivenTheSameName_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getId(), created.getName());
		CurrencyDto updated = currencyService.update(updateDto);

		assertAll("Assert that a valid currency with required fields can be created",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(created.getName(), updated.getName()),
				() -> assertEquals(created.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertTrue(updated.getCreatedAt() != null),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidCurrencyException when trying to update a currency using invalid fields")
	@WithMockUser(username = "user@gmail.com")
	public void GivenInvalidCurrency_WhenUpdating_ThenThrowInvalidCurrencyException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getId(), " ");

		assertThrowsExactly(InvalidCurrencyException.class, () -> currencyService.update(updateDto));
	}

	@Test
	@DisplayName("Throw CurrencyNameUnavailableException when trying to update a currency using an unavailable name")
	@WithMockUser(username = "user@gmail.com")
	public void GivenUnavailableName_WhenUpdating_ThenThrowCurrencyNameUnavailableException() {
		CreateCurrencyDto createDto1 = new CreateCurrencyDto("Tenge");
		CurrencyDto created1 = currencyService.create(createDto1);

		CreateCurrencyDto createDto2 = new CreateCurrencyDto("Dollar");
		CurrencyDto created2 = currencyService.create(createDto2);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created2.getId(), created1.getName());

		assertThrowsExactly(CurrencyNameUnavailableException.class,
				() -> currencyService.update(updateDto));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to update a non-existing currency")
	@WithMockUser(username = "user@gmail.com")
	public void GivenNonExistingCurrency_WhenUpdating_ThenThrowCurrencyNotFoundException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = currencyService.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getId() + 1, "Dollar");

		assertThrowsExactly(CurrencyNotFoundException.class, () -> currencyService.update(updateDto));
	}

	@Test
	@DisplayName("Do not throw when deleting a currency by its ID, regardless if it exists or not")
	@WithMockUser(username = "user@gmail.com")
	public void GivenPossibleCurrency_WhenDeletingItById_ThenDoNotThrow() {
		assertDoesNotThrow(() -> currencyService.deleteById(1L));
	}

	@Test
	@DisplayName("Do not throw when trying to delete a currency by its ID without the authenticated user's email")
	public void GivenWithoutAuthenticatedUserEmail_WhenDeletingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> currencyService.deleteById(1L));
	}

}
