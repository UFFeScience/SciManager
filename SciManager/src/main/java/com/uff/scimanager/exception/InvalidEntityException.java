package com.uff.scimanager.exception;

public class InvalidEntityException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidEntityException(String message) {
        super(message);
    }

    public InvalidEntityException(String message, Throwable throwable) {
        super(message, throwable);
    }

}