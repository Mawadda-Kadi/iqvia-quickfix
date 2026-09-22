package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.TicketDtos;
import com.iqvia.quickfix.entity.Role;
import com.iqvia.quickfix.entity.Ticket;
import com.iqvia.quickfix.entity.TicketStatus;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.InvalidSupportUserException;
import com.iqvia.quickfix.exception.InvalidTicketStatusException;
import com.iqvia.quickfix.exception.TicketNotFoundException;
import com.iqvia.quickfix.repository.TicketRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final MailService mailService;

    public TicketService(
            TicketRepository ticketRepository,
            UserService userService,
            MailService mailService
    ) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    // ----------- Find All Tickets

    public List<TicketDtos.TicketResponse> findAllTickets() {
        List<Ticket> tickets = ticketRepository.findAllByOrderByCreatedAtAsc();
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

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde erstellt",
                "Ihr Ticket \"" + saved.getTitle() + "\" wurde erfolgreich erstellt und hat den Status OPEN."
        );

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

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde aktualisiert",
                "Ihr Ticket \"" + saved.getTitle() + "\" wurde aktualisiert."
        );

        return toTicketResponse(saved);
    }

    // --------- Assign Support

    public TicketDtos.TicketResponse assignSupport(
            Long id,
            Long assignedSupportId
    ) {
        Ticket ticket = getTicketEntityById(id);
        User assignedSupport = userService.getUserEntityById(assignedSupportId);

        if (assignedSupport.getRole() != Role.SUPPORT) {
            throw new InvalidSupportUserException(assignedSupportId);
        }

        ticket.setAssignedSupport(assignedSupport);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket saved = ticketRepository.save(ticket);

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde zugewiesen",
                "Ihr Ticket \"" + ticket.getTitle() + "\" wird jetzt bearbeitet."
        );

        return toTicketResponse(saved);
    }

    // ---------- Resolve Ticket -------- (Add Checks Later)
    public TicketDtos.TicketResponse resolveTicket(Long id) {

        Ticket ticket = getTicketEntityById(id);

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStatusException(
                    "Nur Tickets mit dem Status IN_PROGRESS können als gelöst markiert werden."
            );
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        Ticket saved = ticketRepository.save(ticket);

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde als gelöst markiert",
                "Ihr Ticket \"" + saved.getTitle() + "\" wurde vom Support als gelöst markiert."
        );

        return toTicketResponse(saved);
    }

    // ----------- Reopen Ticket ---- (Add Checks Later)

    public TicketDtos.TicketResponse reopenTicket(Long id) {

        Ticket ticket = getTicketEntityById(id);

        if (ticket.getStatus() != TicketStatus.RESOLVED
                && ticket.getStatus() != TicketStatus.IN_PROGRESS) {

            throw new InvalidTicketStatusException(
                    "Nur Tickets mit dem Status RESOLVED oder IN_PROGRESS können wieder geöffnet werden."
            );
        }

        ticket.setAssignedSupport(null);
        ticket.setStatus(TicketStatus.OPEN);

        Ticket saved = ticketRepository.save(ticket);

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde wieder geöffnet",
                "Ihr Ticket \"" + saved.getTitle() + "\" wurde wieder geöffnet und steht erneut zur Bearbeitung zur Verfügung."
        );

        return toTicketResponse(saved);
    }

    // ----------- Close Ticket ----

    public TicketDtos.TicketResponse closeTicket(Long id) {
        Ticket ticket = getTicketEntityById(id);

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidTicketStatusException(
                    "Nur gelöste Tickets können geschlossen werden."
            );
        }

        ticket.setStatus(TicketStatus.CLOSED);

        Ticket saved = ticketRepository.save(ticket);

        mailService.sendTicketUpdateMail(
                saved.getCreator().getEmail(),
                "Ticket wurde geschlossen",
                "Ihr Ticket \"" + saved.getTitle() + "\" wurde geschlossen."
        );

        return toTicketResponse(saved);
    }

    // ------------ Die hilfsmethoden --------------

    // Sucht ein Ticket anhand der ID und gibt die Ticket-Entity zurück
    public Ticket getTicketEntityById(Long id) {
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
