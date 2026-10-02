package com.liveshield.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FarmNotFoundException extends RuntimeException {

    public FarmNotFoundException(Long farmId) {
        super("Farm not found: " + farmId);
    }
}