package com.dockeep.document.repository;

import com.dockeep.document.entity.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentShareRepository extends JpaRepository<DocumentShare, Long> {
}
