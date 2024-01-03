package com.bt1.qltv1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListOutputResult {
    @Builder.Default
    private long itemsNumber = 0;
    @Builder.Default
    private long totalPage = 0;
    private Long previousPage;
    private Long nextPage;
    private List<?> result;
}
