package com.bt1.qltv1.enumeration;

import lombok.Getter;

public enum ActivateMailType {
    LINK("link"), OTP("otp");

    private final String type;

    ActivateMailType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
