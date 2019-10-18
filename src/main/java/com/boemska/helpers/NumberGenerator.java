package com.boemska.helpers;


import com.boemska.controllers.TicketController;
import com.boemska.data.NumberHolder;
import com.boemska.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NumberGenerator {

    private static volatile NumberGenerator instance;
    private ArrayList<Integer> numbers;
    private ArrayList<Integer> winner;
    private boolean sorted = false;

    private NumberGenerator() {
        this.init();
    }

    private boolean completed = false;
    private Random random = new Random();

    private void init() {
        numbers = new ArrayList<>();
        winner = new ArrayList<>();
        sorted = false;
        completed = false;
        for (int i = 1; i <= 39; i++) {
            numbers.add(i);
        }
        random = new Random();
    }

    public static NumberGenerator getInstance() {
        if (instance == null) {
            synchronized (NumberGenerator.class) {
                if (instance == null) {
                    instance = new NumberGenerator();
                }
            }
        }
        return instance;
    }

    public void reset() {
        init();
    }

    public ArrayList<Integer> generateWinner() {  // works but doesn't make much sense
        if (winner.size() == 7) {
            if (!sorted) {
                Collections.sort(winner);
                sorted = true;
            }
            return winner;
        }
        if (numbers.size() == 32 || winner.size() != 0) {
            init();
        }

        for (int i = 0; i < 7; i++) {
            winner.add(numbers.get(random.nextInt(numbers.size())));
            numbers.remove(winner.get(i));
        }
        Collections.sort(winner);
        return winner;
    }

    public int generateSingle() {
        if (winner.size() == 7) {
            throw new BadRequestException("All numbers have been drawn!");
        }
        if (numbers.size() == 32) {
            init();
        }

        winner.add(numbers.get(random.nextInt(numbers.size())));
        Integer ret = winner.get(winner.size() - 1);
        numbers.remove(ret);
        if (winner.size() == 7) {
            completed = true;
        }
        return ret;
    }

    public NumberHolder getInProgress() {
        if (winner.size() == 0)
            throw new BadRequestException("Drawing hasn't started yet!");
        ArrayList<Integer> tmp = new ArrayList<>();
        tmp.addAll(winner);
        Collections.sort(tmp);
        return new NumberHolder(tmp);
    }

    public NumberHolder getWinningCombination() {
        if (winner.size() < 7) {
            throw new BadRequestException("Drawing hasn't finished yet!");
        }
        return new NumberHolder(winner);
    }

    public int getSingle(int index) {
        if (index > 7 || index < 1) {
            throw new BadRequestException("Not a valid number!");
        }
        if (index > winner.size()) {
            throw new BadRequestException("Number hasn't been drawn yet!");
        }
        return winner.get(index - 1);
    }

    public NumberHolder generateRandomTicket() {
        ArrayList<Integer> available = new ArrayList<>();
        for (int i = 1; i <= 39; i++)
            available.add(i);
        ArrayList<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            numbers.add(available.get(random.nextInt(available.size())));
            available.remove(numbers.get(i));
        }
        Collections.sort(numbers);
        return new NumberHolder(numbers);
    }

    public boolean isCompleted() {
        return completed;
    }
}
