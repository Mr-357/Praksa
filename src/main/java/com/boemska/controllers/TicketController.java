package com.boemska.controllers;
import com.boemska.data.*;
import com.boemska.helpers.CombinationFinder;
import com.boemska.repos.WinnerRepository;
import org.paukov.combinatorics3.Generator;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private List<List<Ticket>> hits = new ArrayList<>();
    private HashSet latest; // latest winner, change name?
    private List<HashSet> sets = new ArrayList<>(); //for intersecting, change name to categories?
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private WinnerRepository winnerRepository;
    private boolean isValid(NumberHolder holder) {
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
    private void loadLatestWinner() {
        if(latest==null)
            latest = new HashSet(new NumberHolder(winnerRepository.getOne((int)winnerRepository.count()).getNumbers()).getNumbers());
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
        for(int i=0;i<39;i++){
            this.sets.add(new HashSet());
        }
        for (Ticket t: this.tickets) {
            NumberHolder tmp = new NumberHolder(t.getNumbers());
            for (int i:tmp.getNumbers()) {
                this.sets.get(i-1).add(t);
            }
        }
    }

    @CrossOrigin()
    @GetMapping("/draw")
    public int draw() {
        int ret = NumberGenerator.getInstance().generateSingle();
        if(NumberGenerator.getInstance().isCompleted()){
            winnerRepository.save(new Winner(NumberGenerator.getInstance().getWinningCombination().getStringNumbers(), LocalDateTime.now()));
            latest=null;
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @CrossOrigin
    @GetMapping("/stats") // TODO: tidy up
    public StatsHolder getStats(@RequestParam String mode){
        hits= new ArrayList<>();
        StatsHolder ret = new StatsHolder();
        ret.total=(int)ticketRepository.count();
        int[] luckiest = new int [39];
        int[] mostpicked =  new int [39];
       // this.prepare();   //   nije potrebno ako je vec izvlaceno??
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
        long start = System.currentTimeMillis();
        if(mode.equals("set"))
        {
            ret.threes=this.findSet(3).size();  // pametniji nacin??
            ret.fours=this.findSet(4).size();
            ret.fives=this.findSet(5).size();
            ret.sixes=this.findSet(6).size();
            ret.sevens=this.findSet(7).size();
        }
        else if (mode.equals("intersect"))
        {
            ret.threes=this.intersect(3).size();
            ret.fours=this.intersect(4).size();
            ret.fives=this.intersect(5).size();
            ret.sixes=this.intersect(6).size();
            ret.sevens=this.intersect(7).size();
        }
        else{
            ret.threes=this.find(3).size();
            ret.fours=this.find(4).size();
            ret.fives=this.find(5).size();
            ret.sixes=this.find(6).size();
            ret.sevens=this.find(7).size();
        }
        long end = System.currentTimeMillis();
        System.out.println("vreme pretrage:"+(end-start));
        Winner upd = winnerRepository.findByNumbers(NumberGenerator.getInstance().getWinningCombination().getStringNumbers()).get(0);
        upd.setThrees(ret.threes); //pametniji nacin???
        upd.setFours(ret.fours);
        upd.setFives(ret.fives);
        upd.setSixes(ret.sixes);
        upd.setSevens(ret.sevens);
        winnerRepository.save(upd);
        return ret;
    }

    //TODO:
    //new controller?
    @CrossOrigin()
    @GetMapping("/list") // double for loop, default collections, doesn't work "realtime"
    public List<Ticket> find(@RequestParam int n) { //rename to find-n, possibly remove
        Winner latest = winnerRepository.getOne((int)winnerRepository.count());
        if(hits.size()==5)
        {
            System.out.println("\n\n\n"+hits.get(n-3).size()+"\n\n\n"+latest.getNumbers()+"\n\n\n");
            return hits.get(n-3);
        }
        if(n==3) {
            hits.add(tickets
                    .parallelStream()
                    .filter(x->CombinationFinder.has(x,new NumberHolder(latest.getNumbers()),n))
                    .collect(Collectors.toList()));
        }
        else{
            hits.add(hits.get(n-4).parallelStream()
                    .filter(x->CombinationFinder.has(x,new NumberHolder(latest.getNumbers()),n))
                    .collect(Collectors.toList()));
            hits.get(n-4).removeAll(hits.get(n-3));
        }

        System.out.println("\n\n\n"+hits.get(n-3).size()+"\n\n\n"+latest.getNumbers()+"\n\n\n");
        return hits.get(n-3);
    }
//////////////////////////////////////////////////////////////////////////////
    @CrossOrigin()
    @GetMapping("/listSet") // hash sets implementation of find(), slightly faster
    public List<Ticket> findSet(@RequestParam int n) {
        this.loadLatestWinner();
        if(hits.size()==5) // if 3,4,5,6,7 sets are found just return what is needed;
        {
            System.out.println("\n\n\n"+hits.get(n-3).size()+"\n\n\n"+latest+"\n\n\n");
            return hits.get(n-3);
        }
        if(n==3) {
            hits.add(tickets
                    .parallelStream()
                    .filter(x->CombinationFinder.hasSet(x,latest,n))
                    .collect(Collectors.toList()));
        }
        else {
            hits.add(hits.get(n-4)
                    .parallelStream()
                    .filter(x->CombinationFinder.hasSet(x,latest,n))
                    .collect(Collectors.toList()));
            hits.get(n-4).removeAll(hits.get(n-3));
        }

        System.out.println("\n\n\n"+hits.get(n-3).size()+"\n\n\n"+latest+"\n\n\n");
        return hits.get(n-3);
    }
/////////////////////////////////////////////////////////////////////////////
    @CrossOrigin()
    @GetMapping("/intersect") //a different approach to find(), can work "realtime" but is slower
    public List<Ticket> intersect(@RequestParam int n){
        NumberHolder win = NumberGenerator.getInstance().getWinningCombination();
        ArrayList<Ticket> ret = new ArrayList<>();
        Consumer<List<Integer>> t = o -> {
            HashSet first = new HashSet(this.sets.get(o.get(0)-1));
            for (int i=1;i<o.size();i++) {
                first.retainAll(this.sets.get(o.get(i)-1));
            }
            ret.addAll(first);
        };
        Generator.combination(win.getNumbers()).simple(n).stream().forEach(t);
        System.out.println(ret.size());
        return ret;
    }

}