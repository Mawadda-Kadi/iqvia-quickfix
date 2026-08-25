package com.iqvia.quickfix.repository;

import com.iqvia.quickfix.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
