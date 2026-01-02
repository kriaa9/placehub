package com.placehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to follow themselves.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SelfFollowException extends RuntimeException {

    public SelfFollowException() {
        super("Users cannot follow themselves");
    }

    public SelfFollowException(String message) {
        super(message);
    }
}
