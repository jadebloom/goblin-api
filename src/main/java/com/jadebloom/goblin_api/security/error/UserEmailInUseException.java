package com.jadebloom.goblin_api.security.error;

public class UserEmailInUseException extends RuntimeException {

    public UserEmailInUseException(String message) {
        super(message);
    }

}
