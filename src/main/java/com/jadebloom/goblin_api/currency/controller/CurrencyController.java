package com.jadebloom.goblin_api.currency.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.service.CurrencyService;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(@Qualifier("currencyServiceImpl") CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public ResponseEntity<CurrencyDto> createCurrency(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto dto = currencyService.save(currencyDto);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CurrencyDto>> findCurrencies(Pageable pageable) {
        Page<CurrencyDto> page = currencyService.findAll(pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrencyDto> findCurrencyById(@PathVariable(name = "id") Long currencyId) {
        CurrencyDto dto = currencyService.findById(currencyId);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<CurrencyDto> fullUpdateCurrencyById(@RequestBody CurrencyDto currencyDto) {
        CurrencyDto dto = currencyService.save(currencyDto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCurrencies() {
        currencyService.deleteAll();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrencyById(@PathVariable(name = "id") Long currencyId) {
        currencyService.deleteById(currencyId);

        return ResponseEntity.noContent().build();
    }

}
