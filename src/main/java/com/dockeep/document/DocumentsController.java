package com.dockeep.document;

import com.dockeep.document.dto.DocumentPageDto;
import com.dockeep.document.entity.Document;
import com.dockeep.document.entity.DocumentVersion;
import com.dockeep.document.request.DocumentUploadRequest;
import com.dockeep.document.response.DocumentUploadResponse;
import com.dockeep.document.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${endpoints.documents.base:/v1/documents}")
@RequiredArgsConstructor
public class DocumentsController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @Valid @ModelAttribute DocumentUploadRequest uploadRequest
    ) throws IOException {
        DocumentVersion version = documentService.uploadFile(uploadRequest);

        Document document = version.getDocument();

        DocumentUploadResponse documentUploadResponse =
                DocumentUploadResponse.builder()
                        .id(document.getId())
                        .title(document.getTitle())
                        .versionNumber(version.getVersionNumber())
                        .s3Path(version.getS3Key())
                        .createdAt(version.getCreatedAt())
                        .tags(document.getTags())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(documentUploadResponse);
    }

    @DeleteMapping
    public ResponseEntity<Integer> deleteDocuments(
            @Valid @RequestBody @NotNull List<@NotEmpty Long> ids){

        Integer deletedDocuments = documentService.deleteAllById(ids);
        return ResponseEntity.ok(deletedDocuments);
    }

    @QueryMapping
    public DocumentPageDto getDocumentsByUserId(@Argument int pageNumber, @Argument int pageSize){
        return documentService.getDocuments(pageNumber, pageSize);
    }

}
