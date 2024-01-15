package com.bt1.qltv1.controller.admin;


import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.genre.GenreRequest;
import com.bt1.qltv1.dto.genre.GenreResponse;
import com.bt1.qltv1.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/genre-management")
public class GenreManagementController {
    private final GenreService genreService;
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
