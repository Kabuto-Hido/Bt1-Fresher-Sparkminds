package com.bt1.qltv1.dto.author;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AuthorRequest {
    private Long id;
    @NotNull(message = "{author.name.null}")
    private String name;
}