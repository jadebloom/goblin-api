package com.jadebloom.goblin_api.account.service;

import com.jadebloom.goblin_api.shared.error.ForbiddenException;

public interface AccountService {

	void deleteAccount() throws ForbiddenException;

}
