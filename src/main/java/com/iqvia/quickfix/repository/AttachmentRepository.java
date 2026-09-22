package com.iqvia.quickfix.repository;

import com.iqvia.quickfix.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTicketIdOrderByUploadedAtAsc(Long ticketId);
}
