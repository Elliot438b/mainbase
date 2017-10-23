package algorithms.sort;

public class Main {
	public static void main(String[] args) {
		int[] array = { 10, 12432, 47, 534, 6, 4576, 47, 56, 8, 34, 84, 37, 38, 233, 537643, 784336, 3456, 282658, 3665,
				3, 82, 1654, 268, 35763, 2344, 63, 38, 43, 22, 40, 0, 60 };
		Sort s = new HeapSort();
		array = s.sort(array);
		s.show(array);
	}
}
