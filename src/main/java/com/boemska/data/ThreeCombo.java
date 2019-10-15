package com.boemska.data;

import java.util.List;
import java.util.Objects;

public class ThreeCombo {
    int n1,n2,n3;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreeCombo that = (ThreeCombo) o;
        return n1 == that.n1 &&
                n2 == that.n2 &&
                n3 == that.n3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, n3);
    }

    public ThreeCombo(List<Integer> numbers) {
        n1=numbers.get(0);
        n2=numbers.get(1);
        n3=numbers.get(2);
    }
}
