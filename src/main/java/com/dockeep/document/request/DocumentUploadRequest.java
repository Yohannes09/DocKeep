package com.dockeep.document.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record DocumentUploadRequest(
        @NotBlank(message = "A title must be provided to upload document.")
        String title,

        List<@NotBlank(message = "A tag must contain at least one character.") String> tags,

        @NotNull(message = "Attach a file.")
        MultipartFile file,

        @Nullable
        Long documentId
) {
}
