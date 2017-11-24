package javaS.genericity.methodS;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MethodDif {
    static Set<String> methods(Class<?> type) {
        Set<String> methodSets = new TreeSet<String>();
        for (Method m : type.getMethods())
            methodSets.add(m.getName());
        return methodSets;
    }

    static void interfaces(Class<?> type) {
        System.out.println("Interfaces in " + type.getSimpleName() + ": ");
        List<String> result = new ArrayList<String>();
        for (Class<?> c : type.getInterfaces())
            result.add(c.getSimpleName());
        System.out.println(result);
    }

    static Set<String> objectMethods = methods(Object.class);// 比较之前要先把根类Object的方法去除。

    static {
        objectMethods.add("clone");
    }

    static void difference(Class<?> setA, Class<?> setB) {
        System.out.println(setA.getSimpleName() + "  " + setB.getSimpleName() + ", adds: ");
        Set<String> comp = SetSupply.difference(methods(setA), methods(setB));
        comp.removeAll(objectMethods);
        System.out.println(comp);
        interfaces(setA);
    }

    public static void main(String[] args) {
        // System.out.println("Collection: " + methods(Collection.class));
        // interfaces(Collection.class);
        // difference(Set.class, Collection.class);
        difference(Set.class, HashSet.class);
        difference(HashSet.class, Set.class);
    }

}
