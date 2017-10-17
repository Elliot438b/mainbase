package algorithms.sort;

public class Client {
	public static void main(String[] args) {
		int[] array = { 1, 12432, 47, 534, 6, 4576, 47, 56, 8 };
		Sort s = new QuickSort();
		array = s.sort(array);
		for (int a : array) {
			System.out.println(a);
		}
	}
}
