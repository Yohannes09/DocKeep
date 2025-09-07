package com.dockeep.document.service;

import com.dockeep.document.dto.DocumentDto;
import com.dockeep.document.dto.DocumentPageDto;
import com.dockeep.document.entity.Document;
import com.dockeep.document.entity.DocumentVersion;
import com.dockeep.document.exception.DocumentNotFoundException;
import com.dockeep.document.mapper.DocumentMapper;
import com.dockeep.document.repository.DocumentRepository;
import com.dockeep.document.request.DocumentUploadRequest;
import com.dockeep.s3.S3Service;
import com.dockeep.user.repository.CachedUserRepository;
import com.dockeep.user.util.PrincipalExtractor;
import com.dockeep.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentMapper documentMapper;
    private final PrincipalExtractor principalExtractor;
    private final S3Service s3Service;
    private final CachedUserRepository cachedUserRepository;
    private final DocumentRepository documentRepository;


    @Transactional
    public DocumentVersion uploadFile(DocumentUploadRequest uploadRequest) throws IOException {
        Long ownerId = principalExtractor.extract().getId();
        User owner = cachedUserRepository.findUserEntityById(ownerId);

        Document document = switch (uploadRequest.documentId()) {
            case null -> Document.builder()
                        .owner(owner)
                        .title(uploadRequest.title())
                        .tags(uploadRequest.tags())
                        .build();

            default -> documentRepository
                    .findById(uploadRequest.documentId())
                    .orElseThrow(()-> new DocumentNotFoundException("The document you are trying to update was not found."));
        };

        int version = switch (document.getDocumentVersions().size()){
            case 0 -> 1;
            default -> document.getDocumentVersions().size();
        };

        String s3Key = s3Service.uploadFile(uploadRequest.file(), uploadRequest.title());

        DocumentVersion documentVersion = DocumentVersion.builder()
                .document(document)
                .versionNumber(version)
                .fileSize(uploadRequest.file().getSize())
                .s3Key(s3Key)
                .mimeType(uploadRequest.file().getContentType())
                .build();

        document.getDocumentVersions().add(documentVersion);
        documentRepository.save(document);

        return documentVersion;
    }

    // Refactor to remove ID and extract from principal instead.
    public DocumentPageDto getDocuments(int pageNumber, int pageSize){
        final String sortByProperty = "createdAt";
        Long ownerId = principalExtractor.extract().getId();

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by(sortByProperty).descending()
        );

        List<DocumentDto> documents = documentRepository
                .findAllByOwnerId(ownerId, pageable)
                .map(documentMapper::entityToDto)
                .toList();

        return DocumentPageDto.builder()
                .documents(documents)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(documents.size())
                .build();
    }

    public int deleteAllById(List<Long> ids){
        return documentRepository.deleteAllById(ids);
    }

}
//if(uploadRequest.documentId() != null){
//document = documentRepository
//        .findById(uploadRequest.documentId())
//        .orElseThrow(()-> new DocumentNotFoundException("The document you are trying to update was not found."));
//version = document.getDocumentVersions().size();
//        }else {
//version = 1;
//document = Document.builder()
//                .owner(owner)
//                .title(uploadRequest.title())
//        .tags(uploadRequest.tags())
//        .build();
//        }