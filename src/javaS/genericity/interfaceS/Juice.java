package javaS.genericity.interfaceS;

public class Juice {
    private static long counter = 0;
    private final long id = counter++;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "....." + id;
    }

}