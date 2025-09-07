package com.dockeep.document.dto;

import com.dockeep.document.entity.DocumentShare;
import com.dockeep.document.entity.DocumentVersion;
import com.dockeep.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record DocumentDto(
        Object id,
        String title,
        LocalDateTime createdAt,
        UserDto owner,
        Set<DocumentShare> shares,
        List<DocumentVersion> documentVersions,
        List<String> tags
) {
}
