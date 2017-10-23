package algorithms.sort;

public class Main {
	public static void main(String[] args) {
		Sort s = new HeapSort();
		int[] array = s.getIntArrayRandom(32, 100);
		array = s.sort(array);
		s.show(array);
	}
}
