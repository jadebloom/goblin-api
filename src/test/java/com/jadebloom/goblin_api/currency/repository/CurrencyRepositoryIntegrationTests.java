package com.jadebloom.goblin_api.currency.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	private UserEntity user;

	@Autowired
	public CurrencyRepositoryIntegrationTests(
			CurrencyRepository underTest,
			UserTestUtils userTestUtils) {
		this.underTest = underTest;

		this.userTestUtils = userTestUtils;
	}

	@BeforeEach
	public void createCurrencyCreator() {
		user = userTestUtils.createUserAndItsDependencies();
	}

	@Test
	@DisplayName("Return currencies found by their creator email")
	public void GivenCurrencies_WhenFindingThemByTheirCreatorEmail_ThenReturnCurrencies() {
		CurrencyEntity toCreate1 = new CurrencyEntity("Tenge", user);
		CurrencyEntity toCreate2 = new CurrencyEntity("Dollar", user);

		CurrencyEntity created1 = underTest.save(toCreate1);
		CurrencyEntity created2 = underTest.save(toCreate2);

		Page<CurrencyEntity> page = underTest.findAllByCreator_Id(
				user.getId(), PageRequest.of(0, 20));

		List<CurrencyEntity> currencies = page.getContent();

		assertAll("Assert that currencies can be found be their creator's email",
				() -> assertEquals(2, currencies.size()),
				() -> assertTrue(currencies.contains(created1)),
				() -> assertTrue(currencies.contains(created2)));
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its name")
	public void GivenCurrency_WhenCheckingItsExistenceByName_ThenReturnTrue() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", user);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByName(created.getName());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Return true when checking the existence of an existing currency by its name, but not ID")
	public void GivenCurrency_WhenCheckingItsExistenceByNameAndNotId_ThenReturnTrue() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", user);
		CurrencyEntity created = underTest.save(toCreate);

		boolean isExists = underTest.existsByIdNotAndName(created.getId() + 1, created.getName());

		assertTrue(isExists);
	}

	@Test
	@DisplayName("Verify that all currencies can be deleted by their creator's ID")
	public void GivenCurrencies_WhenDeletingAllByCreatorId_ThenDelete() {
		CurrencyEntity toCreate = new CurrencyEntity("Tenge", user);
		underTest.save(toCreate);

		underTest.deleteAllByCreator_Id(user.getId());

		List<CurrencyEntity> currencies = underTest.findAll();

		assertEquals(0, currencies.size());
	}

}
