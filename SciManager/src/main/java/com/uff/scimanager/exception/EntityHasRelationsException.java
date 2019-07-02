package com.uff.scimanager.exception;

public class EntityHasRelationsException extends Exception {

	private static final long serialVersionUID = 1L;

	public EntityHasRelationsException(String message) {
        super(message);
    }

    public EntityHasRelationsException(String message, Throwable throwable) {
        super(message, throwable);
    }

}