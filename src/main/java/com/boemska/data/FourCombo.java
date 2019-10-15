package com.boemska.data;

import java.util.List;
import java.util.Objects;

public class FourCombo {
    int n1,n2,n3,n4;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FourCombo fourCombo = (FourCombo) o;
        return n1 == fourCombo.n1 &&
                n2 == fourCombo.n2 &&
                n3 == fourCombo.n3 &&
                n4 == fourCombo.n4;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, n3, n4);
    }

    public FourCombo(List<Integer> numbers) {
        n1=numbers.get(0);
        n2=numbers.get(1);
        n3=numbers.get(2);
        n4=numbers.get(3);
    }
}
