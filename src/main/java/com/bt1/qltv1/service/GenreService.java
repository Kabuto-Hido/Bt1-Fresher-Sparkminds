package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.dto.genre.GenreResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface GenreService {
    GenreResponse save(GenreRequest genreRequest);
    ListOutputResult getAllGenre(Pageable pageable);
}
