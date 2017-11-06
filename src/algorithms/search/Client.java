package algorithms.search;

import tools.XMLUtil;

public class Client {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ST<String, String> st;
        Object sst = XMLUtil.getBean("sf");
        st = new ST<String, String>((SFunction<String, String>) sst);
        System.out.println(st);
        System.out.println(st.get(""));
    }
}
