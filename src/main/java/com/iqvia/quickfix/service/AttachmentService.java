package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.AttachmentDtos;
import com.iqvia.quickfix.entity.Attachment;
import com.iqvia.quickfix.entity.Ticket;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.AttachmentNotFoundException;
import com.iqvia.quickfix.exception.FileStorageException;
import com.iqvia.quickfix.exception.InvalidAttachmentException;
import com.iqvia.quickfix.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketService ticketService;
    private final UserService userService;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            TicketService ticketService,
            UserService userService
    ) {
        this.attachmentRepository = attachmentRepository;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    // ------------ Find Attachments By Ticket ID

    public List<AttachmentDtos.AttachmentResponse> findAttachmentsByTicketId(Long ticketId) {

        ticketService.getTicketEntityById(ticketId);

        List<Attachment> attachments = attachmentRepository.findByTicketIdOrderByUploadedAtAsc(ticketId);

        return attachments.stream()
                .map(this::toAttachmentResponse)
                .toList();

    }

    // -------------- Get Attachment By ID

    public Attachment getAttachmentById(Long id) {
        return getAttachmentEntityById(id);
    }

    // ------------ Create Attachment

    public AttachmentDtos.AttachmentResponse createAttachment(
            MultipartFile file,
            Long uploadedById,
            Long ticketId
    ) {
        Ticket ticket = ticketService.getTicketEntityById(ticketId);
        User uploadedBy = userService.getUserEntityById(uploadedById);

        // Datei prüfen
        if (file.isEmpty()) {
            throw new InvalidAttachmentException("Die Datei darf nicht leer sein");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB

        if (file.getSize() > maxFileSize) {
            throw new InvalidAttachmentException("Die Datei ist zu groß.");
        }

        // Get Metadata
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/gif")
                        || contentType.equals("image/webp"))) {

            throw new InvalidAttachmentException("Nur Bilddateien sind erlaubt.");
        }

        // Bytes lesen
        byte[] data;

        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new FileStorageException(e);
        }

        Attachment attachment = new Attachment();

        attachment.setFileName(fileName);
        attachment.setContentType(contentType);
        attachment.setFileSize(fileSize);
        attachment.setData(data);
        attachment.setUploadedBy(uploadedBy);
        attachment.setTicket(ticket);

        Attachment saved = attachmentRepository.save(attachment);

        return toAttachmentResponse(saved);
    }

    // ------------ Delete Attachment

    public void deleteAttachment(Long id) {
        Attachment attachment = getAttachmentEntityById(id);

        attachmentRepository.delete(attachment);
    }

    // ------------ Die hilfsmethoden --------------

    private Attachment getAttachmentEntityById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new AttachmentNotFoundException(id));
    }

    private AttachmentDtos.AttachmentResponse toAttachmentResponse(Attachment attachment) {

        return new AttachmentDtos.AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getUploadedBy().getId(),
                attachment.getUploadedBy().getUsername(),
                attachment.getUploadedAt()

        );
    }
}
