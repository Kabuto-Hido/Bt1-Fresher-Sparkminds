package com.bt1.qltv1.dto.author;

import com.bt1.qltv1.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class AuthorResponse extends BaseEntity {
    private Long id;
    private String name;
}
