package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.dto.TicketDtos;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.service.TicketService;
import com.iqvia.quickfix.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public List<TicketDtos.TicketResponse> findAllTickets() {
        return ticketService.findAllTickets();
    }

    @GetMapping("/{id}")
    public TicketDtos.TicketResponse getTicketById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ticketService.getTicketForUser(
                id,
                authentication.getName()
        );
    }

    @GetMapping("/my")
    public List<TicketDtos.TicketResponse> findMyTickets(
            Authentication authentication
    ) {
        String username = authentication.getName();

        User user = userService.getUserEntityByUsername(username);

        return ticketService.findTicketsForUser(user.getId());
    }

    @PostMapping
    public TicketDtos.TicketResponse createTicket(
            @Valid @RequestBody TicketDtos.CreateTicketRequest request,
            Authentication authentication
    ) {
        return ticketService.createTicket(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public TicketDtos.TicketResponse updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketDtos.UpdateTicketRequest request,
            Authentication authentication
    ) {
        return ticketService.updateTicket(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/assign")
    public TicketDtos.TicketResponse assignSupport(
            @PathVariable Long id,
            @RequestParam Long assignedSupportId
    ) {
        return ticketService.assignSupport(id, assignedSupportId);
    }

    @PatchMapping("/{id}/resolve")
    public TicketDtos.TicketResponse resolveTicket(
            @PathVariable Long id
    ) {
        return ticketService.resolveTicket(id);
    }

    @PatchMapping("/{id}/reopen")
    public TicketDtos.TicketResponse reopenTicket(
            @PathVariable Long id
    ) {
        return ticketService.reopenTicket(id);
    }

    @PatchMapping("/{id}/close")
    public TicketDtos.TicketResponse closeTicket(
            @PathVariable Long id
    ) {
        return ticketService.closeTicket(id);
    }
}
