package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.dto.genre.GenreResponse;
import com.bt1.qltv1.entity.Genre;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.mapper.GenreMapper;
import com.bt1.qltv1.repository.GenreRepository;
import com.bt1.qltv1.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ListOutputResult getAllGenre(Pageable pageable) {
        Page<Genre> genrePage = genreRepository.findAll(pageable);

        return resultPaging(genrePage);
    }

    private ListOutputResult resultPaging(Page<Genre> genres) {
        if (genres.isEmpty()) {
            throw new NotFoundException("No result", "get-all.author.notfound");
        }

        List<GenreResponse> genreResponses = new ArrayList<>();
        for (Genre genre : genres) {
            genreResponses.add(GenreMapper.toGenreResponse(genre));
        }

        ListOutputResult genreResult = new ListOutputResult(genres.getTotalElements(),
                genres.getTotalPages(), null, null, genreResponses);

        if (genres.hasNext()) {
            genreResult.setNextPage((long) genres.nextPageable().getPageNumber() + 1);
        }
        if (genres.hasPrevious()) {
            genreResult.setPreviousPage((long) genres.previousPageable().getPageNumber() + 1);
        }

        return genreResult;
    }
}
