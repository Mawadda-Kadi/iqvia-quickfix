package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.dto.CommentDtos;
import com.iqvia.quickfix.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDtos.CommentResponse> findCommentsByTicketId (
            @PathVariable Long ticketId
    ) {
        return commentService.findCommentsByTicketId(ticketId);
    }

    @PostMapping
    public CommentDtos.CommentResponse createComment(
            @Valid @RequestBody CommentDtos.CreateCommentRequest request,
            @RequestParam Long authorId,
            @PathVariable Long ticketId
    ) {
        return commentService.createComment(request, authorId, ticketId);
    }
}
