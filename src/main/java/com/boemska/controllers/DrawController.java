package com.boemska.controllers;

import com.boemska.data.*;
import com.boemska.helpers.NumberGenerator;
import com.boemska.repos.TicketRepository;
import com.boemska.repos.WinnerRepository;
import org.paukov.combinatorics3.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@RestController
public class DrawController {
    @Autowired
    private WinnerRepository winnerRepository;
    @Autowired
    private TicketRepository ticketRepository;
    private final SimpMessagingTemplate template;
    @Autowired
    DrawController(SimpMessagingTemplate template){
        this.template=template;
    }
    Map<ThreeCombo, ArrayList<Ticket>> threeComboTicketMap = new HashMap<>();
    Map<FourCombo, ArrayList<Ticket>> fourComboTicketMap = new HashMap<>();
    Map<FiveCombo, ArrayList<Ticket>> fiveComboTicketMap = new HashMap<>();
    Map<SixCombo, ArrayList<Ticket>> sixComboTicketMap = new HashMap<>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private void loadAll() {
        tickets = (ArrayList<Ticket>) ticketRepository.findAll();
    }
    public int draw() {
        int ret = NumberGenerator.getInstance().generateSingle();
        if (NumberGenerator.getInstance().isCompleted()) {
            winnerRepository.save(new Winner(NumberGenerator.getInstance().getWinningCombination().getStringNumbers(), LocalDateTime.now()));
        }
        return ret;
    }
//    @CrossOrigin()
//    @GetMapping("/prepare")
    public void prepare() {
        this.loadAll();
        for (Ticket t : this.tickets) {
            for (int i = 3; i <= 6; i++) {
                Consumer<List<Integer>> save = x -> {
                    switch (x.size()) {
                        case 3:
                            ArrayList<Ticket> tickets = threeComboTicketMap.computeIfAbsent(new ThreeCombo(x), y -> new ArrayList<>());
                            tickets.add(t);
                            break;
                        case 4:
                            fourComboTicketMap.computeIfAbsent(new FourCombo(x), y -> new ArrayList<Ticket>());
                            fourComboTicketMap.get(new FourCombo(x)).add(t);
                            break;
                        case 5:
                            fiveComboTicketMap.computeIfAbsent(new FiveCombo(x), y -> new ArrayList<Ticket>());
                            fiveComboTicketMap.get(new FiveCombo(x)).add(t);
                            break;
                        case 6:
                            sixComboTicketMap.computeIfAbsent(new SixCombo(x), y -> new ArrayList<Ticket>());
                            sixComboTicketMap.get(new SixCombo(x)).add(t);
                            break;
                        case 7:
                            break;
                        default:
                            throw new RuntimeException("bad combination");
                    }
                };
                Generator.combination(new NumberHolder(t.getNumbers()).getNumbers()).simple(i).forEach(save);
            }

        }
//        for(int i=0;i<39;i++){
//            this.sets.add(new HashSet());
//        }
//        for (Ticket t: this.tickets) {
//            NumberHolder tmp = new NumberHolder(t.getNumbers());
//            for (int i:tmp.getNumbers()) {
//                this.sets.get(i-1).add(t);
//            }
//        }
    }


    @MessageMapping("/draw/start")
    public void start() throws InterruptedException {
        System.out.println("received message");
        for(int i=0;i<7;i++){
            this.template.convertAndSend("/draw",this.draw());
            Thread.sleep(1000);
        }
    }
    @CrossOrigin
    @GetMapping("/reset")
    public void reset() {
        NumberGenerator.getInstance().reset();
        threeComboTicketMap = new HashMap<>();
        fourComboTicketMap = new HashMap<>();
        fiveComboTicketMap = new HashMap<>();
        sixComboTicketMap = new HashMap<>();
    }
    //TODO: optimizacija: ucitavanje tiketa koji imaju svoj hash preko baze, ne cuvati sve u memoriji
    private List<Ticket> hashFind(@RequestParam int n) {
        NumberHolder win = NumberGenerator.getInstance().getInProgress();  //change to get inprogress
        HashSet<Ticket> ret = new HashSet<>(); //remove ?
        Consumer<List<Integer>> find = o -> {
            switch (o.size()) {
                case 3:
                    if (threeComboTicketMap.containsKey(new ThreeCombo(o)))
                        ret.addAll(threeComboTicketMap.get(new ThreeCombo(o)));
                    break;
                case 4:
                    if (fourComboTicketMap.containsKey(new FourCombo(o)))
                        ret.addAll(fourComboTicketMap.get(new FourCombo(o)));
                    break;
                case 5:
                    if (fiveComboTicketMap.containsKey(new FiveCombo(o)))
                        ret.addAll(fiveComboTicketMap.get(new FiveCombo(o)));
                    break;
                case 6:
                    if (sixComboTicketMap.containsKey(new SixCombo(o)))
                        ret.addAll(sixComboTicketMap.get(new SixCombo(o)));
                    break;   // null pointer
                default:
                    break;
            }
        };
        Generator.combination(win.getNumbers()).simple(n).stream().forEach(find);  // ovo n menjamo sa inprogress.size ili da ovo nekako zamenimo resenjem iz find?
        System.out.println(ret.size());
        ArrayList<Ticket> fin = new ArrayList<>();
        fin.addAll(ret);
        return fin;
    }
//    @CrossOrigin
//    @GetMapping("/stats") // TODO: tidy up
    public StatsHolder getStats() {
        StatsHolder ret = new StatsHolder();
        ret.total = (int) ticketRepository.count();
        int[] luckiest = new int[39];
        int[] mostpicked = new int[39];
        // this.prepare();   //   nije potrebno ako je vec izvlaceno??
        List<Winner> winners = winnerRepository.findAll();
        for (int i = 0; i < 39; i++) {
            luckiest[i] = 0;
            mostpicked[i] = 0;
        }
        for (Ticket t : tickets) {
            NumberHolder tmp = new NumberHolder(t.getNumbers());
            for (int n : tmp.getNumbers()) {
                mostpicked[n - 1]++;
            }
        }
        for (Winner w : winners) {
            NumberHolder tmp = new NumberHolder(w.getNumbers());
            for (int n : tmp.getNumbers()) {
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
        ret.luckiest = retlucky;
        ret.mostPicked = retpick;
        long start = System.currentTimeMillis();
        ret.threes = this.hashFind(3).size();
        ret.fours = this.hashFind(4).size();
        ret.fives = this.hashFind(5).size();
        ret.sixes = this.hashFind(6).size();
        ret.sevens = this.ticketRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).size();

        long end = System.currentTimeMillis();
        System.out.println("vreme pretrage:" + (end - start));
        Winner upd = winnerRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).get(0);
        upd.setThrees(ret.threes); //pametniji nacin???
        upd.setFours(ret.fours);
        upd.setFives(ret.fives);
        upd.setSixes(ret.sixes);
        upd.setSevens(ret.sevens);
        winnerRepository.save(upd);
        return ret;
    }
}
