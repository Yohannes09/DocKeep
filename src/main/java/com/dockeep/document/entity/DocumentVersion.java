package com.dockeep.document.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DocumentVersion {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "version_id_sequence"
    )
    @SequenceGenerator(
            name = "version_id_sequence",
            sequenceName = "version_id_sequence",
            initialValue = 11_957_103,
            allocationSize = 9
    )
    private Long id;

    @ManyToOne
    private Document document;

    @Column(name = "version_number", nullable = false, updatable = false)
    private Integer versionNumber;

    @Column(name = "s3_path", nullable = false, updatable = false)
    private String s3Key;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastModified;

    @Column(name = "file_size", nullable = false, updatable = false)
    private Long fileSize;

    private String mimeType;

}
