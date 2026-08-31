package com.iqvia.quickfix.exception;


public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {

        super("Ticket mit ID " + id + " nicht gefunden");
    }
}
