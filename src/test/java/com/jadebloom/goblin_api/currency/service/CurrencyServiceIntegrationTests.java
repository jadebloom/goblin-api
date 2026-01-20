package com.jadebloom.goblin_api.currency.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.dto.DeleteCurrenciesDto;
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

	private final CurrencyService underTest;

	private final UserTestUtils userTestUtils;

	private UserEntity user;

	@Autowired
	public CurrencyServiceIntegrationTests(
			CurrencyService underTest,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

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
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidCurrencyWithAllFields_WhenCreating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		createDto.setAlphabeticalCode("KZT");

		CurrencyDto created = underTest.create(createDto);

		assertAll("Assert that a valid currency with all fields can be created",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getAlphabeticalCode(), created.getAlphabeticalCode()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Return currency when creating it with all required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidCurrencyWithRequiredFields_WhenCreating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		CurrencyDto created = underTest.create(createDto);

		assertAll("Assert that a valid currency with required fields can be created",
				() -> assertNotNull(created.getId()),
				() -> assertEquals(createDto.getName(), created.getName()),
				() -> assertEquals(createDto.getAlphabeticalCode(), created.getAlphabeticalCode()),
				() -> assertNotNull(created.getCreatedAt()),
				() -> assertEquals(user.getId(), created.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidCurrencyException when trying to create an invalid currency")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidCurrency_WhenCreating_ThenThrowInvalidCurrencyException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto(" ");

		assertThrowsExactly(InvalidCurrencyException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw CurrencyNameUnavailableException when trying to create a currency with an unavailable name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenCreating_ThenThrowCurrencyNameUnavailableException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		underTest.create(createDto);

		assertThrowsExactly(CurrencyNameUnavailableException.class,
				() -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to create a currency without having the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenCreating_ThenThrowForbiddenException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");

		assertThrowsExactly(ForbiddenException.class, () -> underTest.create(createDto));
	}

	@Test
	@DisplayName("Return a page when finding the authenticated user currencies, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenAnyNumberOfCurrencies_WhenFindingAuthenticatedUserCurrencies_ThenReturnPage() {
		Page<CurrencyDto> page = underTest.findAuthenticatedUserCurrencies(
				PageRequest.of(0, 20));

		assertAll(
				"Assert that a page is returned when finding the authenticated user currencies, regardless if they exist or not",
				() -> assertNotNull(page),
				() -> assertNotNull(page.getContent()),
				() -> assertEquals(0, page.getContent().size()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to find the authenticated user currencies without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenFindingAuthenticatedUserCurrencies_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class,
				() -> underTest.findAuthenticatedUserCurrencies(PageRequest.of(0, 20)));
	}

	@Test
	@DisplayName("Return a currency when finding it by its ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingCurrency_WhenFindingItById_ThenReturnIt() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		CurrencyDto found = underTest.findById(created.getId());

		assertEquals(created, found);
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to find a non-existing currency by its ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenFindingById_ThenThrowCurrencyNotFoundException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> underTest.findById(created.getId() + 1));
	}

	@Test
	@DisplayName("Return a currency when updating using all fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidCurrencyWithAllFields_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto("Dollar");
		updateDto.setAlphabeticalCode("KZT");
		CurrencyDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that a currency can be updated using all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(updateDto.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Return a currency when updating using only required fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenValidCurrencyWithOnlyRequiredFields_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto("Dollar");
		CurrencyDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that a currency can be updated using all fields",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(updateDto.getName(), updated.getName()),
				() -> assertEquals(created.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Return a currency when updating using the same name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenUpdating_ThenReturnCurrency() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created.getName());
		CurrencyDto updated = underTest.update(created.getId(), updateDto);

		assertAll("Assert that a valid currency with required fields can be created",
				() -> assertEquals(created.getId(), updated.getId()),
				() -> assertEquals(created.getName(), updated.getName()),
				() -> assertEquals(created.getAlphabeticalCode(), updated.getAlphabeticalCode()),
				() -> assertEquals(created.getCreatedAt(), updated.getCreatedAt()),
				() -> assertEquals(created.getCreatorId(), updated.getCreatorId()));
	}

	@Test
	@DisplayName("Throw InvalidCurrencyException when trying to update a currency using invalid fields")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenInvalidCurrency_WhenUpdating_ThenThrowInvalidCurrencyException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(" ");

		assertThrowsExactly(InvalidCurrencyException.class,
				() -> underTest.update(created.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw CurrencyNameUnavailableException when trying to update a currency using an unavailable name")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenUnavailableName_WhenUpdating_ThenThrowCurrencyNameUnavailableException() {
		CreateCurrencyDto createDto1 = new CreateCurrencyDto("Tenge");
		CurrencyDto created1 = underTest.create(createDto1);

		CreateCurrencyDto createDto2 = new CreateCurrencyDto("Dollar");
		CurrencyDto created2 = underTest.create(createDto2);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto(created1.getName());

		assertThrowsExactly(CurrencyNameUnavailableException.class,
				() -> underTest.update(created2.getId(), updateDto));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to update a non-existing currency")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenUpdating_ThenThrowCurrencyNotFoundException() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		UpdateCurrencyDto updateDto = new UpdateCurrencyDto("Dollar");

		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> underTest.update(created.getId() + 1, updateDto));
	}

	@Test
	@DisplayName("Do not throw when deleting all currencies, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleCurrencies_WhenDeletingAll_ThenDoNotThrow() {
		CreateCurrencyDto createDto1 = new CreateCurrencyDto("Tenge");
		CreateCurrencyDto createDto2 = new CreateCurrencyDto("Dollar");

		underTest.create(createDto1);
		underTest.create(createDto2);

		underTest.deleteAll();

		Page<CurrencyDto> page =
				underTest.findAuthenticatedUserCurrencies(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all currencies without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAll_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting all currencies by ID, regardless if they exist or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenPossibleCurrencies_WhenDeletingAllById_ThenDoNotThrow() {
		CreateCurrencyDto createDto1 = new CreateCurrencyDto("Tenge");
		CreateCurrencyDto createDto2 = new CreateCurrencyDto("Dollar");

		CurrencyDto created1 = underTest.create(createDto1);
		CurrencyDto created2 = underTest.create(createDto2);

		DeleteCurrenciesDto deleteDto =
				new DeleteCurrenciesDto(List.of(created1.getId(), created2.getId()));

		underTest.deleteAllById(deleteDto);

		Page<CurrencyDto> page =
				underTest.findAuthenticatedUserCurrencies(PageRequest.of(0, 20));

		assertEquals(0, page.getContent().size());
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete all currencies by ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingAllById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteAll());
	}

	@Test
	@DisplayName("Do not throw when deleting a currency by its ID, regardless if it exists or not")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenExistingCurrency_WhenDeletingItById_ThenThrowDoNotThrow() {
		CreateCurrencyDto createDto = new CreateCurrencyDto("Tenge");
		CurrencyDto created = underTest.create(createDto);

		assertDoesNotThrow(() -> underTest.deleteById(created.getId()));
	}

	@Test
	@DisplayName("Throw ForbiddenException when trying to delete a currency by its ID without the authenticated user")
	public void GivenWithoutAuthenticatedUser_WhenDeletingById_ThenThrowForbiddenException() {
		assertThrowsExactly(ForbiddenException.class, () -> underTest.deleteById(1L));
	}

	@Test
	@DisplayName("Throw CurrencyNotFoundException when trying to delete a non-existing currency by ID")
	@WithUserDetails(value = "user@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void GivenNonExistingCurrency_WhenDeletingById_ThenThrowCurrencyNotFoundException() {
		assertThrowsExactly(CurrencyNotFoundException.class,
				() -> underTest.deleteById(1L));
	}

}
