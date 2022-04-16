package io.analu.microservicemeetup.tddrestapimeetup.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
