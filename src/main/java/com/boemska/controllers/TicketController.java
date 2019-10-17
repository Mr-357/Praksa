package com.boemska.controllers;

import com.boemska.data.*;
import com.boemska.helpers.CombinationFinder;
import com.boemska.repos.WinnerRepository;
import org.paukov.combinatorics3.Generator;
import org.springframework.data.domain.Pageable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.boemska.exceptions.BadRequestException;
import com.boemska.exceptions.NotFoundException;
import com.boemska.helpers.NumberGenerator;
import com.boemska.repos.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
public class TicketController {


    private List<Integer> checker = new ArrayList<Integer>(); //helper structure for validating tickets
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private WinnerRepository winnerRepository;

    private boolean isValid(NumberHolder holder) {
        for (Integer i : holder.getNumbers()) {
            if (i < 0 || i > 39) {
                return false;
            }
            if (checker.contains(i)) {
                checker.clear();
                return false;
            }
            checker.add(i);
        }
        checker.clear();
        return true;
    }

    @CrossOrigin()
    @RequestMapping(value = "/register", method = RequestMethod.POST) // tickets.add???
    public IDHolder register(@RequestBody NumberHolder holder) throws Exception {
        if (holder.getNumbers().size() == 7 && isValid(holder)) {
            Ticket ticket = new Ticket(holder.getStringNumbers());
            ticketRepository.save(ticket);
            //tickets.add(ticket);
            return new IDHolder(ticket.getId());
        } else {
            throw new BadRequestException("Not a valid ticket!");
        }

    }

    @CrossOrigin()
    @GetMapping("/tickets")
    public List<Ticket> page(@RequestParam(required = false) Integer n) {
        if (n == null || n == 0)
            n = 1;
        Pageable page = PageRequest.of(n - 1, 24, Sort.by("created").descending());

        return ticketRepository.findAll(page).getContent();
    }

    @CrossOrigin()
    @GetMapping("/pages")
    public int pages() {
        return ((int) ticketRepository.count()) / 24 + 1;
    }

    @CrossOrigin()
    @GetMapping("/tickets/single")
    public Ticket getTicket(@RequestParam String id) {
        Ticket t = ticketRepository.findById(id).orElseThrow(() -> new NotFoundException("Ticket doesn't exist!"));

        return t;

    }

    @CrossOrigin()
    @GetMapping("/numbers/{number}")
    public int getNumber(@PathVariable Integer number) {
        return NumberGenerator.getInstance().getSingle(number);
    }

    @CrossOrigin
    @GetMapping("/register/random") // tickets.add
    public void generateTickets(@RequestParam int n) {
        ArrayList<Ticket> tosave = new ArrayList<Ticket>();
        for (int i = 0; i < n; i++) {
            Ticket ticket = new Ticket(NumberGenerator.getInstance().generateRandomTicket().getStringNumbers());
            tosave.add(ticket);
            //tickets.add(ticket);
        }
        ticketRepository.saveAll(tosave);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







}