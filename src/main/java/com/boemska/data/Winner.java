package com.boemska.data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.toList;

@Entity
@Table(name="winners")
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT",length = 20,nullable = false)
    private String numbers;

    @Column
    private LocalDateTime drawDate;

    @Column
    private int threes;

    @Column
    private int fours;

    @Column
    private int fives;

    @Column
    private int sixes;

    @Column
    private int sevens;
    public Winner(){}
    public Winner(String combo,LocalDateTime drawDate,int threes,int fours,int fives,int sixes,int sevens){
        this.setNumbers(combo);
        this.drawDate=drawDate;
        this.setThrees(threes);
        this.setFours(fours);
        this.setFives(fives);
        this.setSixes(sixes);
        this.setSevens(sevens);
    }
    public Winner(String combo,LocalDateTime drawDate) {
        this.setNumbers(combo);
        this.drawDate = drawDate;
    }

    public String getNumbers() {
        return numbers;
    }

    public ArrayList<Integer> getNumberList(){
       ArrayList tmp = (ArrayList<Integer>) Arrays.asList(numbers.split(","))
                .stream()
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(toList());
        Collections.sort(tmp);
        return tmp;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public int getThrees() {
        return threes;
    }

    public void setThrees(int threes) {
        this.threes = threes;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public int getFives() {
        return fives;
    }

    public void setFives(int fives) {
        this.fives = fives;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public int getSevens() {
        return sevens;
    }

    public void setSevens(int sevens) {
        this.sevens = sevens;
    }
}
