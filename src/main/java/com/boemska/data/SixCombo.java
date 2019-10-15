package com.boemska.data;

import java.util.List;
import java.util.Objects;

public class SixCombo {
    int n1,n2,n3,n4,n5,n6;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SixCombo sixCombo = (SixCombo) o;
        return n1 == sixCombo.n1 &&
                n2 == sixCombo.n2 &&
                n3 == sixCombo.n3 &&
                n4 == sixCombo.n4 &&
                n5 == sixCombo.n5 &&
                n6 == sixCombo.n6;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, n3, n4, n5, n6);
    }

    public SixCombo(List<Integer> numbers) {
        n1=numbers.get(0);
        n2=numbers.get(1);
        n3=numbers.get(2);
        n4=numbers.get(3);
        n5=numbers.get(4);
        n6=numbers.get(5);
    }
}
