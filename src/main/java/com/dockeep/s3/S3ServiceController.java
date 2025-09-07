package com.dockeep.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${endpoints.files.base:/v1/files}")
@RequiredArgsConstructor
public class S3ServiceController {
    private final S3Service s3Service;

    @GetMapping("/{key}")
    public ResponseEntity<String> getPresignedObjectRequest(@PathVariable @NotBlank String key){
        String signedGetUrl = s3Service.generateGetPresignedURL(key);
        return ResponseEntity.ok(signedGetUrl);
    }

}
