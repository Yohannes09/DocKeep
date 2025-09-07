package com.dockeep.document.mapper;

import com.dockeep.document.dto.DocumentDto;
import com.dockeep.document.entity.Document;
import com.dockeep.user.util.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
public interface DocumentMapper {
    DocumentDto entityToDto(Document document);
}
