package com.flextech.building.common.webservice;

public class ChildExistException extends RuntimeException {
    public ChildExistException() {
        super();
    }

    public ChildExistException(String message) {
        super(message);
    }
}
