package com.jadebloom.goblin_api.currency.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

@DataJpaTest(showSql = false)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CurrencyRepositoryIntegrationTests {

    private final CurrencyRepository underTest;

    @Autowired
    public CurrencyRepositoryIntegrationTests(CurrencyRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void canCreateAndFindCurrencies() {
        CurrencyEntity e1 = new CurrencyEntity("American Dollar");
        e1.setAlphabeticalCode("USD");
        CurrencyEntity e2 = new CurrencyEntity("日本の円");

        CurrencyEntity savedE1 = underTest.save(e1);
        CurrencyEntity savedE2 = underTest.save(e2);

        Page<CurrencyEntity> page = underTest.findAll(PageRequest.of(0, 20));
        List<CurrencyEntity> entities = page.getContent();

        assertAll(
                "Assert that currencies can be created and found",
                () -> assertEquals(2, entities.size()),
                () -> assertTrue(entities.contains(savedE1)),
                () -> assertTrue(entities.contains(savedE2)));
    }

    @Test
    public void canCreateCurrencyAndFindItById() {
        CurrencyEntity e = new CurrencyEntity("American Dollar");
        e.setAlphabeticalCode("USD");

        CurrencyEntity savedE = underTest.save(e);

        Optional<CurrencyEntity> foundE = underTest.findById(savedE.getId());

        assertAll(
                "Assert that a currency can be created and found",
                () -> assertTrue(foundE.isPresent()),
                () -> assertEquals(savedE, foundE.get()));
    }

    @Test
    public void canCheckCurrencyForExistenceByName() {
        CurrencyEntity e = new CurrencyEntity("Dollar");
        CurrencyEntity savedE = underTest.save(e);

        boolean b1 = underTest.existsByName(savedE.getName());
        boolean b2 = underTest.existsByName(savedE.getName() + 1);

        assertAll("Assert that currencies can be checked for existence by name",
                () -> assertTrue(b1),
                () -> assertFalse(b2));
    }

    @Test
    public void canCheckCurrencyForExistenceByIdNotAndName() {
        CurrencyEntity e = new CurrencyEntity("Dollar");
        CurrencyEntity savedE = underTest.save(e);

        boolean b1 = underTest.existsByIdNotAndName(savedE.getId(), savedE.getName());
        boolean b2 = underTest.existsByIdNotAndName(savedE.getId() + 1, savedE.getName());

        assertAll("Assert that currencies can be checked for existence by name and not id",
                () -> assertFalse(b1),
                () -> assertTrue(b2));
    }

    @Test
    public void canUpdateCurrencyAndFindItById() {
        CurrencyEntity e = new CurrencyEntity("Tenge");
        CurrencyEntity savedE = underTest.save(e);

        savedE.setName("Dollar");
        underTest.save(savedE);

        Optional<CurrencyEntity> foundE = underTest.findById(savedE.getId());

        assertAll(
                "Assert that a currency can be updated and found",
                () -> assertTrue(foundE.isPresent()),
                () -> assertEquals(savedE, foundE.get()));
    }

    @Test
    public void canDeleteCurrencyById() {
        CurrencyEntity e = new CurrencyEntity("Dollar");
        Long id = underTest.save(e).getId();

        underTest.deleteById(id);

        Optional<CurrencyEntity> foundE = underTest.findById(id);

        assertTrue(foundE.isEmpty());
    }

}
