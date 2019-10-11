package com.boemska.helpers;

import com.boemska.data.NumberHolder;
import com.boemska.data.Ticket;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
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

}
