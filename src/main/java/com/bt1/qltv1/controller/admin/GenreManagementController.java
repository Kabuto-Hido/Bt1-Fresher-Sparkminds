package com.bt1.qltv1.controller.admin;


import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.dto.genre.GenreResponse;
import com.bt1.qltv1.service.GenreService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/genre-management")
public class GenreManagementController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public ResponseEntity<ListOutputResult> getAllGenre(@PageableDefault(sort = Global.DEFAULT_SORT_BY, direction = Sort.Direction.DESC)
                                                         Pageable pageable) {
        return ResponseEntity.ok(genreService.getAllGenre(pageable));
    }

    @PostMapping("/genres")
    public ResponseEntity<SuccessResponseDTO> createNewGenre(@Valid @RequestBody
                                                             GenreRequest request) {
        genreService.save(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.CREATED,
                "Add new genre successful!"));
    }

    @PutMapping("/genres/{id}")
    public ResponseEntity<GenreResponse> updateGenre(@PathVariable long id,
                                                      @Valid @RequestBody GenreRequest request) {
        request.setId(id);
        return ResponseEntity.ok(genreService.save(request));
    }
}
