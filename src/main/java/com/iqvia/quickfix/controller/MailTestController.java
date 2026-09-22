package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.service.MailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class MailTestController {

    private final MailService mailService;

    public MailTestController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/mail")
    public String testMail() {
        mailService.sendTicketUpdateMail(
                "mawadda.kadi@gmail.com",
                "QuickFix Test",
                "Die E-Mail-Funktion funktioniert."
        );

        return "Mail wurde gesendet.";
    }
}
