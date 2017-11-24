package javaS.genericity;

public class TwoTuple<A, B> {
    public final A a;
    public final B b;

    public TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a + "__&&&__" + b;
    }

    public static void main(String[] args) {
        TwoTuple<Integer, Integer> two = new TwoTuple<Integer, Integer>(110, 211);
        System.out.println(two);
    }
}
