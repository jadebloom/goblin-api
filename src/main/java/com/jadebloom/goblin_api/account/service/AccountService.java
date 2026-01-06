package com.jadebloom.goblin_api.account.service;

import com.jadebloom.goblin_api.account.dto.UpdatePasswordDto;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface AccountService {

	void updatePassword(UpdatePasswordDto updateDto)
			throws ForbiddenException, IncorrectPasswordException, IncorrectPasswordException;

	void deleteAccount() throws ForbiddenException;

}
