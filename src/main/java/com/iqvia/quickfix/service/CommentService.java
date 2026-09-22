package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.CommentDtos;
import com.iqvia.quickfix.entity.Comment;
import com.iqvia.quickfix.entity.Ticket;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.CommentNotFoundException;
import com.iqvia.quickfix.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketService ticketService;
    private final UserService userService;
    private final MailService mailService;

    public CommentService(
            CommentRepository commentRepository,
            TicketService ticketService,
            UserService userService,
            MailService mailService
    ) {
        this.commentRepository = commentRepository;
        this.ticketService = ticketService;
        this.userService = userService;
        this.mailService = mailService;
    }

    // ----------- Find Comments By Ticket ID

    public List<CommentDtos.CommentResponse> findCommentsByTicketId(Long ticketId) {

        ticketService.getTicketEntityById(ticketId);

        List<Comment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);

        return comments.stream()
                .map(this::toCommentResponse)
                .toList();
    }

    // ------------- Create Comment

    public CommentDtos.CommentResponse createComment(
            CommentDtos.CreateCommentRequest request,
            Long authorId,
            Long ticketId
    ) {
        Ticket ticket = ticketService.getTicketEntityById(ticketId);
        User author = userService.getUserEntityById(authorId);

        Comment comment = new Comment();
        comment.setText(request.text());
        comment.setAuthor(author);
        comment.setTicket(ticket);

        Comment saved = commentRepository.save(comment);

        mailService.sendTicketUpdateMail(
                ticket.getCreator().getEmail(),
                "Neuer Kommentar zu Ihrem Ticket",
                "Zu Ihrem Ticket \"" + ticket.getTitle() + "\" wurde ein neuer Kommentar hinzugefügt."
        );

        return toCommentResponse(saved);
    }


    // ------------ Die hilfsmethoden --------------

    private Comment getCommentEntityById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    private CommentDtos.CommentResponse toCommentResponse(Comment comment) {

        return new CommentDtos.CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt()

        );
    }
}
