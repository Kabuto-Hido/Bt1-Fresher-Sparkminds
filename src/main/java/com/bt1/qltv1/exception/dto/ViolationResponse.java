package com.bt1.qltv1.exception.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViolationResponse {
    private String fieldName;
    private String message;
    private String messageCode;
}
