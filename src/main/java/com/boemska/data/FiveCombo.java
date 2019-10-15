package com.boemska.data;

import java.util.List;
import java.util.Objects;

public class FiveCombo {
    int n1,n2,n3,n4,n5;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiveCombo fiveCombo = (FiveCombo) o;
        return n1 == fiveCombo.n1 &&
                n2 == fiveCombo.n2 &&
                n3 == fiveCombo.n3 &&
                n4 == fiveCombo.n4 &&
                n5 == fiveCombo.n5;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, n3, n4, n5);
    }

    public FiveCombo(List<Integer> numbers) {
        n1=numbers.get(0);
        n2=numbers.get(1);
        n3=numbers.get(2);
        n4=numbers.get(3);
        n5=numbers.get(4);
    }
}
