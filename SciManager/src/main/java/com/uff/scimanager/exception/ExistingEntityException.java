package com.uff.scimanager.exception;

public class ExistingEntityException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExistingEntityException(String message) {
        super(message);
    }

    public ExistingEntityException(String message, Throwable throwable) {
        super(message, throwable);
    }

}