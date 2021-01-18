package uwu.smsgamer.pasteclient.utils;

import java.util.Objects;

public class Pair<A, B> {
    public A a;
    public B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Pair() {
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "Pair{" +
          "a=" + a +
          ", b=" + b +
          '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) &&
          Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
