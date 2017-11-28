package javaS.genericity;

import java.util.*;

class Hey<YYYY, B, C, D> {
}

public class Client {
    public static void main(String[] args) {
        Class<?> c1 = new ArrayList<String>().getClass();
        Class<?> c2 = new ArrayList<Integer>().getClass();
        Class<?> c5 = new HashSet<Integer>().getClass();
        Class<?> c6 = new HashMap<Integer, String>().getClass();
        Class<?> c7 = new Hey<Integer, String, Integer, String>().getClass();
        System.out.println(Arrays.toString(c1.getTypeParameters()));
        System.out.println(Arrays.toString(c2.getTypeParameters()));
        System.out.println(Arrays.toString(c5.getTypeParameters()));
        System.out.println(Arrays.toString(c6.getTypeParameters()));
        System.out.println(Arrays.toString(c7.getTypeParameters()));
    }
}
