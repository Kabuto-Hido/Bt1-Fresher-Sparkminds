package com.bt1.qltv1.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException{
    private final String errorCode;
    public TokenException(String message,String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
