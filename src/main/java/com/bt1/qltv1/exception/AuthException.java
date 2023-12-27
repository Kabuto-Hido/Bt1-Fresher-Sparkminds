package com.bt1.qltv1.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{
    private final String errorCode;
    public AuthException(String message,String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
