package algorithms.sort;

public class Main {
	public static void main(String[] args) {
		int[] array = { 1, 12432, 47, 534, 6, 4576, 47, 56, 8 };
		Sort s = new HeapSort();
		array = s.sort(array);
		s.show(array);
	}
}
