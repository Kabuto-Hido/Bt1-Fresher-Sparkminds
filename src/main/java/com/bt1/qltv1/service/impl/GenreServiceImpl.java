package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.dto.genre.GenreResponse;
import com.bt1.qltv1.entity.Genre;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.GenreMapper;
import com.bt1.qltv1.repository.GenreRepository;
import com.bt1.qltv1.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public GenreResponse save(GenreRequest genreRequest) {
        Genre genre = new Genre();
        if (genreRequest.getId() != null) {
            genre = genreRepository.findById(genreRequest.getId()).orElseThrow(() ->
                    new NotFoundException("Not found genre with id " + genreRequest.getId(),
                            "genre.id.not-exist"));
        }
        genre.setName(genreRequest.getName());
        genre = genreRepository.save(genre);

        return GenreMapper.toGenreResponse(genre);
    }
}
