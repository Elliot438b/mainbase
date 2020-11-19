package leecode.gastation;

public class Solution {
	public int canCompleteCircuit(int[] gas, int[] cost) {
		int length = gas.length;
		for (int i = 0; i < length; i++) {
			int balance = gas[i];
			int nextIndex = i + 1;
			boolean flag = false;
			boolean over = false;
			for (int j = nextIndex; j < length + nextIndex; j++) {
				int step = j - 1;
				if (j >= length) {
					j -= length;
					if (step >= length) {
						step -= length;
					}

					over = true;
				}
				balance -= cost[step];
				if (!over && balance <= 0) {
					flag = true;
					break;
				}
				balance += gas[j];
				if (!flag && over && j == i) {
					if (balance == gas[j]) {
						return i;
					} else {
						break;
					}
				}
			}

		}
		return -1;
	}

	public static void main(String[] args) {
		Solution s = new Solution();
//		int[] gas = { 1, 2, 3, 4, 5 };
//		int[] cost = { 3, 4, 5, 1, 2 };
		int[] gas={2,3,4};
		int[] cost={3,4,3};
		System.out.println(s.canCompleteCircuit(gas, cost));
	}
}
