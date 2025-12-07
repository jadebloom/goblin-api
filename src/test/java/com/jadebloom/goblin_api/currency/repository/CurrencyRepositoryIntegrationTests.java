package com.jadebloom.goblin_api.currency.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
    public void canCreateAndFindMultipleCurrencies() {
        CurrencyEntity entity1 = new CurrencyEntity("American Dollar", "USD");
        CurrencyEntity entity2 = new CurrencyEntity("Australian Dollar");
        CurrencyEntity entity3 = new CurrencyEntity("日本の円");

        CurrencyEntity savedEntity1 = underTest.save(entity1);
        CurrencyEntity savedEntity2 = underTest.save(entity2);
        CurrencyEntity savedEntity3 = underTest.save(entity3);

        Page<CurrencyEntity> page = underTest.findAll(PageRequest.of(0, 5));

        List<CurrencyEntity> entities = page.getContent();

        assertAll(
                "Assert that multiple currencies can be created and found",
                () -> assertEquals(3, entities.size()),
                () -> assertTrue(entities.contains(savedEntity1)),
                () -> assertTrue(entities.contains(savedEntity2)),
                () -> assertTrue(entities.contains(savedEntity3)));
    }

    @Test
    public void canCreateAndFindCurrency() {
        CurrencyEntity entity = new CurrencyEntity("American Dollar", "USD");

        CurrencyEntity savedEntity = underTest.save(entity);

        Optional<CurrencyEntity> foundEntity = underTest.findById(savedEntity.getId());

        assertAll(
                "Assert that a currency can be created and found",
                () -> assertTrue(foundEntity.isPresent()),
                () -> assertEquals(savedEntity, foundEntity.get()));
    }

    @Test
    public void canCreateAndCheckTheExistenceOfCurrency() {
        CurrencyEntity entity = new CurrencyEntity("Dollar");

        CurrencyEntity savedEntity = underTest.save(entity);

        boolean isExists = underTest.existsById(savedEntity.getId());

        assertTrue(isExists);
    }

    @Test
    public void canUpdateAndFindCurrency() {
        CurrencyEntity entity = new CurrencyEntity("Ruble");
        CurrencyEntity savedEntity = underTest.save(entity);

        savedEntity.setName("Dollar");
        underTest.save(savedEntity);

        Optional<CurrencyEntity> foundEntity = underTest.findById(savedEntity.getId());

        assertAll(
                "Assert that a currency can be updated and found",
                () -> assertTrue(foundEntity.isPresent()),
                () -> assertEquals(savedEntity, foundEntity.get()));
    }

    @Test
    public void canDeleteAllCurrencies() {
        CurrencyEntity e1 = new CurrencyEntity("Dollar");
        CurrencyEntity e2 = new CurrencyEntity("Ruble");

        underTest.save(e1);
        underTest.save(e2);

        underTest.deleteAll();

        List<CurrencyEntity> entities = new ArrayList<>();

        Iterable<CurrencyEntity> iterable = underTest.findAll();
        iterable.forEach(entities::add);

        assertTrue(entities.isEmpty());
    }

    @Test
    public void canDeleteCurrencyById() {
        CurrencyEntity entity = new CurrencyEntity("Dollar");

        Long id = underTest.save(entity).getId();

        underTest.deleteById(id);

        Optional<CurrencyEntity> foundEntity = underTest.findById(id);

        assertTrue(foundEntity.isEmpty());
    }

}
