package algorithms.sort;

public class BubbleSort extends Sort {
    public int[] sort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length - 1; j++) {
                if (array[j] < array[j + 1]) {
                    array = swap(array, j, j + 1);
                }
            }
        }
        return array;
    }
}
