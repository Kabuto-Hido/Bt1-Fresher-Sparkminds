package com.bt1.qltv1.dto.author;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AuthorRequest {
    private Long id;
    @NotBlank(message = "Author name can not be null")
    private String name;
}