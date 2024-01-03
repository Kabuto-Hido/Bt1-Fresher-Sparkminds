package com.bt1.qltv1.dto.genre;

import com.bt1.qltv1.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class GenreResponse extends BaseEntity {
    private Long id;
    private String name;
}
