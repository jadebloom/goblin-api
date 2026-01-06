package com.jadebloom.goblin_api.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jadebloom.goblin_api.account.dto.UpdatePasswordDto;
import com.jadebloom.goblin_api.account.service.AccountService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PreAuthorize("hasRole('USER')")
	@PatchMapping
	public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordDto updateDto) {
		accountService.updatePassword(updateDto);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping
	public ResponseEntity<Void> deleteAccount() {
		accountService.deleteAccount();

		return ResponseEntity.noContent().build();
	}

}
