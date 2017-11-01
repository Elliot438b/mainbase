package algorithms.sort;

public class Client {
    public static void main(String[] args) {
        Sort s = new MergeSort();
        int[] array = s.getIntArrayRandom(32, 120);
        array = s.sort(array);
        s.show(array);
    }
}
