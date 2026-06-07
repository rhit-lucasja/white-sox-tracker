package com.soxtracker.exceptions;

public class MlbApiException extends RuntimeException {
    public MlbApiException(int status) {
        super("MLB API returned error status: " + status);
    }
}
