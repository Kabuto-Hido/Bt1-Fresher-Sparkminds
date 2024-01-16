package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.dto.author.AuthorRequest;
import com.bt1.qltv1.dto.author.AuthorResponse;
import com.bt1.qltv1.service.AuthorService;
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
@RequestMapping("/api/v1/admin/author-management")
@RequiredArgsConstructor
public class AuthorManagementController {
    private final AuthorService authorService;

    @GetMapping("/authors")
    public ResponseEntity<ListOutputResult> getAllAuthor(@PageableDefault(sort = Global.DEFAULT_SORT_BY, direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        return ResponseEntity.ok(authorService.getAllAuthor(pageable));
    }

    @PostMapping("/authors")
    public ResponseEntity<SuccessResponseDTO> createNewAuthor(@Valid @RequestBody
                                                                  AuthorRequest request) {
        authorService.save(request);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.CREATED,
                "Add new author successful!"));
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable long id,
                                                       @Valid @RequestBody AuthorRequest request) {
        request.setId(id);
        return ResponseEntity.ok(authorService.save(request));
    }
}
