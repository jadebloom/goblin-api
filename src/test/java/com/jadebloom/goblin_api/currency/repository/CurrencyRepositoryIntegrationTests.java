package com.jadebloom.goblin_api.currency.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.test.PermissionTestUtils;
import com.jadebloom.goblin_api.security.test.RoleTestUtils;
import com.jadebloom.goblin_api.security.test.UserTestUtils;

@DataJpaTest(showSql = false)
@Import({ UserTestUtils.class, RoleTestUtils.class, PermissionTestUtils.class })
public class CurrencyRepositoryIntegrationTests {

	private final CurrencyRepository underTest;

	private final UserTestUtils userTestUtils;

	private UserEntity currencyCreator;

	@Autowired
	public CurrencyRepositoryIntegrationTests(
			CurrencyRepository underTest,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createCurrencyCreator() {
		currencyCreator = userTestUtils.createUserAndItsDependencies();
	}

	@Test
	@DisplayName("Return currencies found by their creator email")
	public void GivenCurrencies_WhenFindingThemByTheirCreatorEmail_ThenReturnCurrencies() {
		CurrencyEntity toCreate1 = new CurrencyEntity("Tenge", currencyCreator);
		CurrencyEntity toCreate2 = new CurrencyEntity("Dollar", currencyCreator);

		CurrencyEntity created1 = underTest.save(toCreate1);
		CurrencyEntity created2 = underTest.save(toCreate2);

		Page<CurrencyEntity> page = underTest.findAllByCreator_Email(currencyCreator.getEmail(), PageRequest.of(0, 20));

		List<CurrencyEntity> currencies = page.getContent();

		assertAll("Assert that currencies can be found be their creator's email",
				() -> assertEquals(2, currencies.size()),
				() -> assertTrue(currencies.contains(created1)),
				() -> assertTrue(currencies.contains(created2)));
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its name")
	public void GivenCurrency_WhenCheckingItsExistenceByName_ThenReturnTrue() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", currencyCreator);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByName(created.getName());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its name, but not ID")
	public void GivenCurrency_WhenCheckingItsExistenceByNameAndNotId_ThenReturnTrue() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", currencyCreator);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByIdNotAndName(created.getId() + 1, created.getName());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its name and creator's email")
	public void GivenCurrency_WhenCheckingItsExistenceByNameAndCreatorEmail_ThenReturnTrue() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", currencyCreator);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByIdAndCreator_Email(
				created.getId(),
				currencyCreator.getEmail());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return false when checking the existence of an existing currency by its name and other creator's email")
	public void GivenCurrency_WhenCheckingItsExistenceByNameAndOtherCreatorEmail_ThenReturnFalse() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", currencyCreator);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByIdAndCreator_Email(
				created.getId(),
				currencyCreator.getEmail() + "m");

		assertFalse(isExists);
	}

}
