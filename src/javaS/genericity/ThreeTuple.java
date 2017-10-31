package javaS.genericity;

public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
    public final C c;

    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }

    @Override
    public String toString() {
        return a + "__&&&__" + b + "__&&&__" + c;
    }

    public static void main(String[] args) {
        ThreeTuple<Integer, Integer, Integer> three = new ThreeTuple<Integer, Integer, Integer>(110, 211, 985);
        System.out.println(three);
    }
}
