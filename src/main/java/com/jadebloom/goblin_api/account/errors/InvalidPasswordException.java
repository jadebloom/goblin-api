package com.jadebloom.goblin_api.account.errors;

public class InvalidPasswordException extends RuntimeException {

	public InvalidPasswordException(String message) {
		super(message);
	}

}
