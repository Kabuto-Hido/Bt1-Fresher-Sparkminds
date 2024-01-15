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
    @NotBlank(message = "{genre.name.null}")
    private String name;
}
