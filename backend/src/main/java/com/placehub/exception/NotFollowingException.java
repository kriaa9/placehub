package com.placehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to unfollow someone they don't follow.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFollowingException extends RuntimeException {

    public NotFollowingException(String message) {
        super(message);
    }

    public NotFollowingException(Long followerId, Long followingId) {
        super("User " + followerId + " is not following user " + followingId);
    }
}
