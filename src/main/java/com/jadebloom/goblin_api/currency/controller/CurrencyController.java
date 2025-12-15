package com.jadebloom.goblin_api.currency.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jadebloom.goblin_api.currency.dto.CreateCurrencyDto;
import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.service.CurrencyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(@Qualifier("currencyServiceImpl") CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurrencyDto> createCurrency(@Valid @RequestBody CreateCurrencyDto createDto) {
        CurrencyDto created = currencyService.create(createDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CurrencyDto>> findCurrencies(Pageable pageable) {
        Page<CurrencyDto> page = currencyService.findAll(pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurrencyDto> findCurrencyById(@PathVariable(name = "id") Long currencyId) {
        CurrencyDto found = currencyService.findById(currencyId);

        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurrencyDto> updateCurrency(@Valid @RequestBody CurrencyDto dto) {
        CurrencyDto updated = currencyService.update(dto);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/all")
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
