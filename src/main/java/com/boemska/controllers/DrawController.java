package com.boemska.controllers;

import com.boemska.data.*;
import com.boemska.helpers.NumberGenerator;
import com.boemska.repos.TicketRepository;
import com.boemska.repos.WinnerRepository;
import org.paukov.combinatorics3.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
//postoji mogucnost da ovo ne radi kako treba za mnogo klijenata zbog multithreadinga pri ovim zahtevima, mozda je potrebno napraviti @Bean-ove za tickets,stats,started
//i proveriti da li ovaj websocket salje svima nezavisno od toga koji je kontroler pozvan
@RestController
public class DrawController {
    @Autowired
    private WinnerRepository winnerRepository;
    @Autowired
    private TicketRepository ticketRepository;
    private final SimpMessagingTemplate template;
    @Autowired
    private TicketsBean tickets;
    @Autowired
    DrawController(SimpMessagingTemplate template) {
        this.template = template;
    }
    //jedna ready varijabla? kad se zavrsi prepare

    private ArrayList<StatsHolder> stats = new ArrayList<>();
    private void loadAll() {
        tickets.setTickets((ArrayList<Ticket>) ticketRepository.findAll());
    }

    public int draw() {
        int ret = NumberGenerator.getInstance().generateSingle();
        if (NumberGenerator.getInstance().isCompleted()) {
            winnerRepository.save(new Winner(NumberGenerator.getInstance().getWinningCombination().getStringNumbers(), LocalDateTime.now()));
        }
        return ret;
    }

    @CrossOrigin()
    @GetMapping("/prepare")
    //TODO: load on startup, update on ticketadd?
    public void prepare() {
        if (tickets.getThreeComboTicketMap().size() != 0) // temporary workaround
            return;
        this.loadAll();
        for (Ticket ticket : this.tickets.getTickets()) {
            for (int comboLength = 3; comboLength <= 6; comboLength++) {
                Consumer<List<Integer>> save = combination -> {
                    ArrayList<Ticket> temporaryMap;
                    switch (combination.size()) {
                        case 3:
                            temporaryMap = this.tickets.getThreeComboTicketMap().computeIfAbsent(new ThreeCombo(combination), element -> new ArrayList<>());
                            temporaryMap.add(ticket);
                            break;
                        case 4:
                            temporaryMap=this.tickets.getFourComboTicketMap().computeIfAbsent(new FourCombo(combination), element -> new ArrayList<>());
                            temporaryMap.add(ticket);
                            break;
                        case 5:
                            temporaryMap=this.tickets.getFiveComboTicketMap().computeIfAbsent(new FiveCombo(combination), element -> new ArrayList<>());
                            temporaryMap.add(ticket);
                            break;
                        case 6:
                            temporaryMap=this.tickets.getSixComboTicketMap().computeIfAbsent(new SixCombo(combination), element -> new ArrayList<>());
                            temporaryMap.add(ticket);
                            break;
                        case 7:
                            break;
                        default:
                            throw new RuntimeException("bad combination");
                    }
                };
                Generator.combination(ticket.getNumberList()).simple(comboLength).forEach(save);
            }

        }
    }


    @MessageMapping("/draw/start")
    public void start() throws InterruptedException {
        System.out.println("received message");
        for (int i = 0; i < 7; i++) {
            StatsHolder tmp = this.getStats(this.draw());
            this.stats.add(tmp);
            this.template.convertAndSend("/draw", tmp);
            Thread.sleep(2000);
        }
    }

    @SubscribeMapping("/draw")
    public List<StatsHolder> onSubscribe(){
        System.out.println("client subscribed");
        return this.stats;
    }
    @CrossOrigin
    @GetMapping("/reset")
    public void reset() {
        NumberGenerator.getInstance().reset();
        stats.clear();
    }

    private List<Ticket> hashFind(@RequestParam int comboLength) {
        NumberHolder drawnNumbers = NumberGenerator.getInstance().getInProgress();
        HashSet<Ticket> currentSearch = new HashSet<>();
        ArrayList<Ticket> completedSearch = new ArrayList<>();
        Consumer<List<Integer>> find = combination -> {
            switch (combination.size()) {
                case 3:
                    if (this.tickets.getThreeComboTicketMap().containsKey(new ThreeCombo(combination)))
                        currentSearch.addAll(this.tickets.getThreeComboTicketMap().get(new ThreeCombo(combination)));
                    break;
                case 4:
                    if (this.tickets.getFourComboTicketMap().containsKey(new FourCombo(combination)))
                        currentSearch.addAll(this.tickets.getFourComboTicketMap().get(new FourCombo(combination)));
                    break;
                case 5:
                    if (this.tickets.getFiveComboTicketMap().containsKey(new FiveCombo(combination)))
                        currentSearch.addAll(this.tickets.getFiveComboTicketMap().get(new FiveCombo(combination)));
                    break;
                case 6:
                    if (this.tickets.getSixComboTicketMap().containsKey(new SixCombo(combination)))
                        currentSearch.addAll(this.tickets.getSixComboTicketMap().get(new SixCombo(combination)));
                    break;
                default:
                    break;
            }
        };
        int currentlyDrawn = drawnNumbers.getNumbers().size();
        if (comboLength > currentlyDrawn) {
            return completedSearch;
        }
        Generator.combination(drawnNumbers.getNumbers()).simple(comboLength).stream().forEach(find);
        System.out.println(currentSearch.size());

        completedSearch.addAll(currentSearch);
        return completedSearch;
    }

    // TODO: tidy up
    public StatsHolder getStats(int lastDrawnNumber) {
        StatsHolder currentStats = new StatsHolder();
        currentStats.lastDrawn = lastDrawnNumber;
        currentStats.total = (int) ticketRepository.count();
        int[] luckiest = new int[39];
        int[] mostpicked = new int[39];
        // this.prepare();   //   nije potrebno ako je vec izvlaceno??
        List<Winner> winners = winnerRepository.findAll();
        for (int i = 0; i < 39; i++) {
            luckiest[i] = 0;
            mostpicked[i] = 0;
        }
        for (Ticket t : tickets.getTickets()) {
            for (int n : t.getNumberList()) {
                mostpicked[n - 1]++;
            }
        }
        for (Winner w : winners) {
            for (int n : w.getNumberList()) {
                luckiest[n - 1]++;
            }
        }
        int retlucky = 1;
        int retpick = 1;
        for (int i = 0; i < 39; i++) {
            if (luckiest[i] > luckiest[retlucky - 1]) {
                retlucky = i + 1;
            }
            if (mostpicked[i] > mostpicked[retpick - 1]) {

                retpick = i + 1;
            }
        }
        currentStats.luckiest = retlucky;
        currentStats.mostPicked = retpick;
        long start = System.currentTimeMillis();
        currentStats.threes = this.hashFind(3).size();
        currentStats.fours = this.hashFind(4).size();
        currentStats.fives = this.hashFind(5).size();
        currentStats.sixes = this.hashFind(6).size();
        if (NumberGenerator.getInstance().isCompleted()) {
            currentStats.sevens = this.ticketRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).size();
            Winner currentCombination = winnerRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).get(0);
            currentCombination.setThrees(currentStats.threes); //pametniji nacin???
            currentCombination.setFours(currentStats.fours);
            currentCombination.setFives(currentStats.fives);
            currentCombination.setSixes(currentStats.sixes);
            currentCombination.setSevens(currentStats.sevens);
            winnerRepository.save(currentCombination);
        }
        long end = System.currentTimeMillis();
        System.out.println("vreme pretrage:" + (end - start) + NumberGenerator.getInstance().getInProgress().getNumbers());
        return currentStats;
    }
}
