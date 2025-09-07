package com.dockeep.document.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DocumentUploadResponse(
        Object id,
        String title,
        int versionNumber,
        String s3Path,
        LocalDateTime createdAt,
        List<String> tags
) {
}
