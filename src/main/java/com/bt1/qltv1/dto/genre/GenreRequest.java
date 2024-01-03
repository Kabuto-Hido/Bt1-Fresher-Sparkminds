package com.bt1.qltv1.dto.genre;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class GenreRequest {
    private Long id;
    @NotBlank(message = "Genre name can not be null")
    private String name;
}
