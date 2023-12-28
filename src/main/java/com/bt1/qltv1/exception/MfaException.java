package com.bt1.qltv1.exception;

import lombok.Getter;

@Getter
public class MfaException extends RuntimeException{
    private final String errorCode;
    public MfaException(String message,String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
