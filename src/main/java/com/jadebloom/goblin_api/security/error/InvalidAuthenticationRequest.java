package com.jadebloom.goblin_api.security.error;

public class InvalidAuthenticationRequest extends RuntimeException {

	public InvalidAuthenticationRequest(String message) {
		super(message);
	}

}
