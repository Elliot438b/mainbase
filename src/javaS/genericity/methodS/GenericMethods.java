package javaS.genericity.methodS;

import java.util.List;
import java.util.Map;

import algorithms.sort.Sort;
import pattern.adapter.Grade;

public class GenericMethods {
    public <T> void f(T x) {
        System.out.println(x.getClass().getName());
    }

    public static void main(String[] args) {
        GenericMethods gm = new GenericMethods();
        Map<?, ?> pet = Container.map();
        gm.f(11111231224L);
        gm.f(231);
        gm.f("zxcva");
        gm.f('c');
        gm.f(pet);
        gm.f(Container.<Sort, List<Grade>>map());
    }
}
