package pattern.adapter;

public class Grade {

    public int[] getRankList(int[] index) {
        System.out.println("效率较低的算法");
        return index;
    }
}
