package com.boemska.data;

import org.paukov.combinatorics3.Generator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class TicketsBean {
    private Map<ThreeCombo, ArrayList<Ticket>> threeComboTicketMap = new HashMap<>();
    private Map<FourCombo, ArrayList<Ticket>> fourComboTicketMap = new HashMap<>();
    private Map<FiveCombo, ArrayList<Ticket>> fiveComboTicketMap = new HashMap<>();
    private Map<SixCombo, ArrayList<Ticket>> sixComboTicketMap = new HashMap<>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
//    @Bean
//    @Scope("singleton")
//    public TicketsBean getInstance(){
//        return new TicketsBean();
//    }
    public TicketsBean(){

    }

    public Map<FourCombo, ArrayList<Ticket>> getFourComboTicketMap() {
        return fourComboTicketMap;
    }

    public void setFourComboTicketMap(Map<FourCombo, ArrayList<Ticket>> fourComboTicketMap) {
        this.fourComboTicketMap = fourComboTicketMap;
    }

    public Map<ThreeCombo, ArrayList<Ticket>> getThreeComboTicketMap() {
        return threeComboTicketMap;
    }

    public void setThreeComboTicketMap(Map<ThreeCombo, ArrayList<Ticket>> threeComboTicketMap) {
        this.threeComboTicketMap = threeComboTicketMap;
    }

    public Map<FiveCombo, ArrayList<Ticket>> getFiveComboTicketMap() {
        return fiveComboTicketMap;
    }

    public void setFiveComboTicketMap(Map<FiveCombo, ArrayList<Ticket>> fiveComboTicketMap) {
        this.fiveComboTicketMap = fiveComboTicketMap;
    }

    public Map<SixCombo, ArrayList<Ticket>> getSixComboTicketMap() {
        return sixComboTicketMap;
    }

    public void setSixComboTicketMap(Map<SixCombo, ArrayList<Ticket>> sixComboTicketMap) {
        this.sixComboTicketMap = sixComboTicketMap;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicketWithHash(Ticket t){
        for (int i = 3; i <= 6; i++) {
            Consumer<List<Integer>> save = numberList -> {
                ArrayList<Ticket> temporaryMap;
                switch (numberList.size()) {
                    case 3:
                        temporaryMap = this.getThreeComboTicketMap().computeIfAbsent(new ThreeCombo(numberList), y -> new ArrayList<>());
                        temporaryMap.add(t);
                        break;
                    case 4:
                        temporaryMap=this.getFourComboTicketMap().computeIfAbsent(new FourCombo(numberList), y -> new ArrayList<>());
                        temporaryMap.add(t);
                        break;
                    case 5:
                        temporaryMap=this.getFiveComboTicketMap().computeIfAbsent(new FiveCombo(numberList), y -> new ArrayList<>());
                        temporaryMap.add(t);
                        break;
                    case 6:
                        temporaryMap=this.getSixComboTicketMap().computeIfAbsent(new SixCombo(numberList), y -> new ArrayList<>());
                        temporaryMap.add(t);
                        break;
                    case 7:
                        break;
                    default:
                        throw new RuntimeException("bad combination");
                }
            };
            Generator.combination(t.getNumberList()).simple(i).forEach(save);
        }
        this.tickets.add(t);
    }
}
