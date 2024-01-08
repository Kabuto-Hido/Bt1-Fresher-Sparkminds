package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.author.AuthorResponse;
import com.bt1.qltv1.entity.Author;

public class AuthorMapper {
    public static AuthorResponse toAuthorResponse(Author author){
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .createdBy(author.getCreatedBy())
                .createdDate(author.getCreatedDate())
                .modifiedBy(author.getModifiedBy())
                .modifiedDate(author.getModifiedDate()).build();
    }
}
