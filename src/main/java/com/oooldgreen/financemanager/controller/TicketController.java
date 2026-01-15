package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.entity.Ticket;
import com.oooldgreen.financemanager.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final FinanceService financeService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = financeService.addTicket(ticket);
        return ResponseEntity.ok(savedTicket);
    }
}
