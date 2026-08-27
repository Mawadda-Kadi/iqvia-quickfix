package com.iqvia.quickfix.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CommentDtos {

    public record CreateCommentRequest(
            @NotBlank String text
    ) {}

    public record CommentResponse(
            Long id,
            String text,
            Long authorId,
            String authorUsername,
            LocalDateTime createdAt
    ) {}
}
