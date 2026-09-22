package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.dto.TicketDtos;
import com.iqvia.quickfix.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<TicketDtos.TicketResponse> findAllTickets() {
        return ticketService.findAllTickets();
    }

    @GetMapping("/{id}")
    public TicketDtos.TicketResponse getTicketById(
            @PathVariable Long id
    ) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public TicketDtos.TicketResponse createTicket(
            @Valid @RequestBody TicketDtos.CreateTicketRequest request,
            @RequestParam Long creatorId
    ) {
        return ticketService.createTicket(request, creatorId);
    }

    @PutMapping("/{id}")
    public TicketDtos.TicketResponse updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketDtos.UpdateTicketRequest request
    ) {
        return ticketService.updateTicket(id, request);
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
