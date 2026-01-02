package com.placehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to follow someone they already follow.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyFollowingException extends RuntimeException {

    public AlreadyFollowingException(String message) {
        super(message);
    }

    public AlreadyFollowingException(Long followerId, Long followingId) {
        super("User " + followerId + " is already following user " + followingId);
    }
}
