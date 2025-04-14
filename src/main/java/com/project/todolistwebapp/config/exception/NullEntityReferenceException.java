package com.project.todolistwebapp.config.exception;

public class NullEntityReferenceException extends RuntimeException{
    public NullEntityReferenceException() {}

    public NullEntityReferenceException(String message) {super(message);}
}
