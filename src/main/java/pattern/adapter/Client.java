package pattern.adapter;

public class Client {
    public static void main(String[] args) {
        Grade g = new GradeAdapter();
        int[] grades = { 78, 75, 91, 81, 67, 32, 60, 59, 100, 74, 75 };
        g.getRankList(grades);
    }

}
