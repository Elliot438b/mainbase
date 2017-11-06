package algorithms.search;

import tools.XMLUtil;

public class Client {
    public static void main(String[] args) {
        Object oSf = XMLUtil.getBean("sf");
        @SuppressWarnings("unchecked")
        ST<String, String> st = new ST<String, String>((SFunction<String, String>) oSf);
        System.out.println(st);
        System.out.println(st.get(""));
    }
}
