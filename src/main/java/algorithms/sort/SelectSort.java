package algorithms.sort;

public class SelectSort extends Sort {
    public int[] sort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {// 控制交换的次数，最多交换n-1次。
            int maxIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] > array[maxIndex]) {
                    maxIndex = j;
                }
            }
            if (maxIndex != i) {// 找到当前位置后面最小值的位置，交换。
                swap(array, maxIndex, i);
            }
        }
        return array;
    }
}
