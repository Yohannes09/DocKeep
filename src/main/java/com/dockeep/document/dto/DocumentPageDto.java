package com.dockeep.document.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DocumentPageDto (
        List<DocumentDto> documents,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
){
}
