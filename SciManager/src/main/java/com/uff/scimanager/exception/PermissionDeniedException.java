package com.uff.scimanager.exception;

public class PermissionDeniedException extends Exception {

	private static final long serialVersionUID = 1L;

	public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable throwable) {
        super(message, throwable);
    }

}