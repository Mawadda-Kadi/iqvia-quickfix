package com.iqvia.quickfix.dto;

import com.iqvia.quickfix.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TicketDtos {

    public record CreateTicketRequest(
            @NotBlank String title,
            String description,
            @NotNull Category category,
            @NotNull Priority priority
    ) {}

    public record UpdateTicketRequest(
            @NotBlank String title,
            String description,
            Category category,
            Priority priority,
            TicketStatus status,
            Long assignedSupportId
    ) {}

    public record TicketResponse(
            Long id,
            String title,
            String description,
            Category category,
            Priority priority,
            TicketStatus status,
            Long creatorId,
            String creatorUsername,
            Long assignedSupportId,
            String assignedSupportUsername,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
