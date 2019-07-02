package com.uff.scimanager.exception;

public class ExpiredTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }

}