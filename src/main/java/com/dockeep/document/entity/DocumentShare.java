package com.dockeep.document.entity;

import com.dockeep.document.constant.DocumentSharePermission;
import com.dockeep.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "document_shares")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentShare {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne
    private User user;

    @ElementCollection
    @CollectionTable(
            name = "document_share_permissions",
            joinColumns = @JoinColumn(name = "document_share_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<DocumentSharePermission> permissions = new HashSet<>();
}
