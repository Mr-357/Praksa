package com.boemska.data;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.toList;

public class NumberHolder {

    private ArrayList<Integer> numbers;

    public NumberHolder(ArrayList<Integer> numbers) {
        this.numbers = numbers;
    }

    public NumberHolder() {
    }
    public NumberHolder(String numbers) {
        this.numbers = (ArrayList<Integer>) Arrays.asList(numbers.split(","))
                .stream()
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(toList());
    }

    public String getStringNumbers() {
        Collections.sort(numbers);
        String ret="";
        for (Integer i: getNumbers()) {
            ret+=i.toString();
            ret+=',';
        }
        ret=ret.substring(0,ret.length()-1);
        return ret;
    }

    public ArrayList<Integer> getNumbers() {
        return numbers;
    }
}