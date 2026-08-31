package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.TicketDtos;
import com.iqvia.quickfix.entity.Role;
import com.iqvia.quickfix.entity.Ticket;
import com.iqvia.quickfix.entity.TicketStatus;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.TicketNotFoundException;
import com.iqvia.quickfix.repository.TicketRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    public TicketService(
            TicketRepository ticketRepository,
            UserService userService
    ) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
    }

    // ----------- Find All Tickets

    public List<TicketDtos.TicketResponse> findAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return tickets.stream()     // geht die Tickets einzeln durch
                .map(this::toTicketResponse)    // wandelt jedes Ticket um
                .toList();      // sammelt die umgewandelten Objekte in einer neuen Liste
    }

    // ------------ Get The Ticket By ID

    public TicketDtos.TicketResponse getTicketById(Long id) {
        Ticket ticket = getTicketEntityById(id);
        return toTicketResponse(ticket);
    }

    // ---------- Create Ticket

    public TicketDtos.TicketResponse createTicket(
            TicketDtos.CreateTicketRequest request,
            Long creatorId
    ) {
        User creator = userService.getUserEntityById(creatorId);

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCategory(request.category());
        ticket.setPriority(request.priority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreator(creator);

        Ticket saved = ticketRepository.save(ticket);

        return toTicketResponse(saved);
    }

    // ---------- Update Ticket

    public TicketDtos.TicketResponse updateTicket(
            Long id,
            TicketDtos.UpdateTicketRequest request
    ) {
        Ticket ticket = getTicketEntityById(id);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCategory(request.category());
        ticket.setPriority(request.priority());

        Ticket saved = ticketRepository.save(ticket);

        return toTicketResponse(saved);
    }

    // --------- Assign Support

    public TicketDtos.TicketResponse assignSupport(
            Long id,
            Long assignedSupportId
    ) {
        Ticket ticket = getTicketEntityById(id);
        User assignedSupport = userService.getUserEntityById(assignedSupportId);

        // InvalidSupportUserException
        if (assignedSupport.getRole() != Role.SUPPORT) {
            throw new IllegalArgumentException("Der Benutzer hat nicht die Rolle SUPPORT");
        }

        ticket.setAssignedSupport(assignedSupport);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket saved = ticketRepository.save(ticket);

        return toTicketResponse(saved);
    }

    // ---------- Resolve Ticket -------- (Add Checks Later)
    public TicketDtos.TicketResponse resolveTicket(Long id) {

        Ticket ticket = getTicketEntityById(id);

        ticket.setStatus(TicketStatus.RESOLVED);
        Ticket saved = ticketRepository.save(ticket);

        return toTicketResponse(saved);
    }

    // ----------- Reopen Ticket ---- (Add Checks Later)

    public TicketDtos.TicketResponse reopenTicket(Long id) {

        Ticket ticket = getTicketEntityById(id);

        ticket.setAssignedSupport(null);
        ticket.setStatus(TicketStatus.OPEN);

        Ticket saved = ticketRepository.save(ticket);

        return toTicketResponse(saved);
    }

    // ------------ Die hilfsmethoden --------------

    // Sucht ein Ticket anhand der ID und gibt die Ticket-Entity zurück
    private Ticket getTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    // Erhält eine Ticket-Entity und gibt ein TicketResponse-DTO zurück.
    private TicketDtos.TicketResponse toTicketResponse(Ticket ticket) {

        return new TicketDtos.TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreator().getId(),
                ticket.getCreator().getUsername(),
                ticket.getAssignedSupport() != null
                    ? ticket.getAssignedSupport().getId() : null,
                ticket.getAssignedSupport() != null
                    ? ticket.getAssignedSupport().getUsername() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
