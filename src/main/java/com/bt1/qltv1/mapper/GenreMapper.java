package com.bt1.qltv1.mapper;

import com.bt1.qltv1.dto.genre.GenreResponse;
import com.bt1.qltv1.entity.Genre;

public class GenreMapper {
    public static GenreResponse toGenreResponse(Genre genre){
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .createdBy(genre.getCreatedBy())
                .createdDate(genre.getCreatedDate())
                .modifiedBy(genre.getModifiedBy())
                .modifiedDate(genre.getModifiedDate()).build();

    }
}
