package com.iqvia.quickfix.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class AttachmentDtos {

    public record CreateAttachmentRequest(
            @NotBlank String fileName
    ) {}

    public record AttachmentResponse(
            Long id,
            String fileName,
            String filePath,
            Long fileSize,
            Long uploadedById,
            String uploadedByUsername,
            LocalDateTime uploadedAt
    ) {}
}
