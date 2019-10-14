package com.boemska.helpers;

import com.boemska.data.NumberHolder;
import com.boemska.data.Ticket;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CombinationFinder {

    public static boolean has(Ticket ticket,NumberHolder winner,int n){

            NumberHolder temp = new NumberHolder(ticket.getNumbers());
            int hits = 0;
           for(int i : temp.getNumbers()) {
               if(winner.getNumbers().contains(i))
                   hits++;
           }

           if(hits>=n)
               return true;
        return false;
    }

    public static boolean hasSet(Ticket t,HashSet winner,int n){
        NumberHolder temp = new NumberHolder(t.getNumbers());
        HashSet ticketSet = new HashSet(temp.getNumbers());
        ticketSet.retainAll(winner);
        return ticketSet.size()>=n;
    }
}
