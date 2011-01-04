package cz.cvut.kbss.owl2query.util;

import java.math.BigInteger;

public class PermutationGenerator {

	private int[] array;
	private BigInteger left;
	private BigInteger total;

	public PermutationGenerator(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("Min 1");
		}
		array = new int[n];
		total = getFactorial(n);
		reset();
	}

	public void reset() {
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		left = new BigInteger(total.toString());
	}

	public BigInteger getNumLeft() {
		return left;
	}

	public BigInteger getTotal() {
		return total;
	}

	public boolean hasMore() {
		return left.compareTo(BigInteger.ZERO) == 1;
	}

	private static BigInteger getFactorial(int n) {
		BigInteger fact = BigInteger.ONE;
		for (int i = n; i > 1; i--) {
			fact = fact.multiply(new BigInteger(Integer.toString(i)));
		}
		return fact;
	}

	public int[] getNext() {

		if (left.equals(total)) {
			left = left.subtract(BigInteger.ONE);
			return array;
		}

		int temp;

		int j = array.length - 2;
		while (array[j] > array[j + 1]) {
			j--;
		}

		int k = array.length - 1;
		while (array[j] > array[k]) {
			k--;
		}

		temp = array[k];
		array[k] = array[j];
		array[j] = temp;

		int r = array.length - 1;
		int s = j + 1;

		while (r > s) {
			temp = array[s];
			array[s] = array[r];
			array[r] = temp;
			r--;
			s++;
		}

		left = left.subtract(BigInteger.ONE);
		return array;

	}

}
