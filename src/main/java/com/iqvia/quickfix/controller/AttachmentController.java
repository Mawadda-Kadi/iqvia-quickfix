package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.dto.AttachmentDtos;
import com.iqvia.quickfix.entity.Attachment;
import com.iqvia.quickfix.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/tickets/{ticketId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public List<AttachmentDtos.AttachmentResponse> findAttachmentsByTicketId(
            @PathVariable Long ticketId
    ) {
        return attachmentService.findAttachmentsByTicketId(ticketId);
    }

    @GetMapping("/{id}")
    public Attachment getAttachmentById(
            @PathVariable Long ticketId,
            @PathVariable Long id
    ) {
        return attachmentService.getAttachmentById(id);
    }

    @PostMapping
    public AttachmentDtos.AttachmentResponse createAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long uploadedById,
            @PathVariable Long ticketId
    ) {
        return attachmentService.createAttachment(file, uploadedById, ticketId);
    }

    @DeleteMapping("/{id}")
    public void deleteAttachment(
            @PathVariable Long ticketId,
            @PathVariable Long id
    ) {
        attachmentService.deleteAttachment(id);
    }
}
