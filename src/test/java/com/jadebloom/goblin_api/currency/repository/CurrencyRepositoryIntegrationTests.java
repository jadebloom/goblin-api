package com.jadebloom.goblin_api.currency.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class CurrencyRepositoryIntegrationTests {

    private final CurrencyRepository underTest;

    @Autowired
    public CurrencyRepositoryIntegrationTests(CurrencyRepository underTest) {
        this.underTest = underTest;
    }

}
