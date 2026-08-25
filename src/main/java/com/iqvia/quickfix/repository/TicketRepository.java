package com.iqvia.quickfix.repository;

import com.iqvia.quickfix.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
