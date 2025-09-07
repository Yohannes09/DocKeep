package com.dockeep.document.repository;

import com.dockeep.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Document document WHERE document.id IN (:ids)")
    int deleteAllById(@Param("ids") List<Long> ids);
}
