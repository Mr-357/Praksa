package com.boemska.controllers;
import com.boemska.data.*;
import com.boemska.helpers.CombinationFinder;
import com.boemska.repos.WinnerRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
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


    private List<Integer> checker = new ArrayList<Integer>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private List<List<Ticket>> winners = new ArrayList<>();
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private WinnerRepository winnerRepository;
    private boolean isValid(NumberHolder holder)
    {
        for (Integer i:holder.getNumbers()) {
            if(i<0 || i>39) {
                return false;
            }
            if(checker.contains(i)) {
                checker.clear();
                return false;
            }
            checker.add(i);
        }
        checker.clear();
        return true;
    }
    private void loadAll() {
        tickets = (ArrayList<Ticket>) ticketRepository.findAll();

    }
    @CrossOrigin()
    @GetMapping("/list")
    public List<Ticket> find(@RequestParam int n) { //rename to find-n
        Winner latest = winnerRepository.getOne((int)winnerRepository.count());
        if(winners.size()==5)
        {
            System.out.println("\n\n\n"+winners.get(n-3).size()+"\n\n\n"+latest.getNumbers()+"\n\n\n");
            return winners.get(n-3);
        }
        if(n==3) {
           winners.add(tickets
                   .parallelStream()
                   .filter(x->CombinationFinder.has(x,new NumberHolder(latest.getNumbers()),n))
                   .collect(Collectors.toList()));
        }
        else{
            winners.add(winners.get(n-4).parallelStream()
                    .filter(x->CombinationFinder.has(x,new NumberHolder(latest.getNumbers()),n))
                    .collect(Collectors.toList()));
            winners.get(n-4).removeAll(winners.get(n-3));
        }

        System.out.println("\n\n\n"+winners.get(n-3).size()+"\n\n\n"+latest.getNumbers()+"\n\n\n");
        return winners.get(n-3);
    }

    @CrossOrigin()
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public IDHolder register(@RequestBody NumberHolder holder) throws Exception{
        if(holder.getNumbers().size() == 7 && isValid(holder)) {
            Ticket ticket = new Ticket(holder.getStringNumbers());
            ticketRepository.save(ticket);
            tickets.add(ticket);
            return new IDHolder(ticket.getId());
        }
        else{
            throw new BadRequestException("Not a valid ticket!");
        }

    }
    @CrossOrigin()
    @GetMapping("/tickets")
    public List<Ticket> page(@RequestParam(required = false) Integer n){
        if(n==null || n==0)
            n=1;
        Pageable page =  PageRequest.of(n-1,24, Sort.by("created").descending());

        return  ticketRepository.findAll(page).getContent();
    }
    
    @CrossOrigin()
    @GetMapping("/pages")
    public int pages(){
        return ((int) ticketRepository.count()) / 24 + 1;
    }

    @CrossOrigin()
    @GetMapping("/tickets/single")
    public Ticket getTicket(@RequestParam String id){
        Ticket t = ticketRepository.findById(id).orElseThrow(()-> new NotFoundException("Ticket doesn't exist!"));

        return t;

    }
    @CrossOrigin()
    @GetMapping("/prepare")
    public void prepare() {
        this.loadAll();
    }

    @CrossOrigin()
    @GetMapping("/draw")
    public int draw() {
        int ret = NumberGenerator.getInstance().generateSingle();
        if(NumberGenerator.getInstance().isCompleted()){
            winnerRepository.save(new Winner(NumberGenerator.getInstance().getWinningCombination().getStringNumbers(), LocalDateTime.now()));
        }
        return ret;
    }
    @CrossOrigin()
    @GetMapping("/numbers/{number}")
    public int getNumber(@PathVariable Integer number){
        return NumberGenerator.getInstance().getSingle(number);
    }
    @CrossOrigin()
    @GetMapping("/winningTicket")
    public ArrayList<Integer> getWinningSet() {
        return NumberGenerator.getInstance().generateWinner();
    }
    @CrossOrigin()
    @GetMapping("/winners")
    public List<Ticket> getWinners(){
        return ticketRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers());
    }
    @CrossOrigin
    @GetMapping("/reset")
    public void reset() {
        NumberGenerator.getInstance().reset();
    }
    @CrossOrigin
    @GetMapping("/register/random")
    public void generateTickets(@RequestParam int n){
        ArrayList<Ticket> tosave = new ArrayList<Ticket>();
        for(int i=0;i<n;i++){
            Ticket ticket = new Ticket(NumberGenerator.getInstance().generateRandomTicket().getStringNumbers());
            tosave.add(ticket);
           tickets.add(ticket);
        }
        ticketRepository.saveAll(tosave);

    }

    @CrossOrigin
    @GetMapping("/stats")
    public StatsHolder getStats(){
        StatsHolder ret = new StatsHolder();
        ret.total=(int)ticketRepository.count();
        int[] luckiest = new int [39];
        int[] mostpicked =  new int [39];
        this.prepare();
        List<Winner> winners = winnerRepository.findAll();
        for(int i=0;i<39;i++){
            luckiest[i]=0;
            mostpicked[i]=0;
        }
        for(Ticket t:tickets){
            NumberHolder tmp = new NumberHolder(t.getNumbers());
            for(int n : tmp.getNumbers())
            {
                mostpicked[n-1]++;
            }
        }
        for(Winner w:winners){
            NumberHolder tmp = new NumberHolder(w.getNumbers());
            for(int n : tmp.getNumbers()){
                luckiest[n-1]++;
            }
        }
        int retlucky=1;
        int retpick=1;
        for(int i=0;i<39;i++)
        {
            if(luckiest[i]>luckiest[retlucky-1]){
                retlucky=i+1;
            }
            if(mostpicked[i]>mostpicked[retpick-1]){

                retpick=i+1;
            }
        }
        ret.luckiest=retlucky;
        ret.mostPicked=retpick;
        ret.threes=this.find(3).size();
        ret.fours=this.find(4).size();
        ret.fives=this.find(5).size();
        ret.sixes=this.find(6).size();
        ret.sevens=this.find(7).size();
        Winner upd = winnerRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).get(0);
        upd.setThrees(ret.threes);
        upd.setFours(ret.fours);
        upd.setFives(ret.fives);
        upd.setSixes(ret.sixes);
        upd.setSevens(ret.sevens);
        winnerRepository.save(upd);
        return ret;
    }








    //TODO:
    //new controller?
    //frontend:
    //  >interface
    //  >components
    //  >routes

}