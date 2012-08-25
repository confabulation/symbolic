package utils;

import java.util.Arrays;

/**
 * Tools for 2D arrays
 * 
 * @author bernard
 * 
 */
public class ArrayTools2D {
	/**
	 * Returns a copy of the elements array[i][j], with i in [a1, b1[ and j in
	 * [a2, b2[
	 * <p>
	 * TODO improve algorithm so to tolerate out of range indexes (see the
	 * advanced test)
	 * </p>
	 * 
	 * @param array
	 *            2D array. External array cannot contain null values and must
	 *            be at least contain a single sub-array.
	 * @param a1
	 *            <= b1
	 * @param a2
	 *            <= b2
	 * @param b1
	 *            all lines from a1 included to b1 excluded are copied
	 * @param b2
	 *            in those lines, only the indexes from a2 included to b2
	 *            excluded are copied
	 * @return the copied rectangle
	 */
	public static <T> T[][] copyOfRectangle(T[][] array, int a1, int a2,
			int b1, int b2) {
		if (b1 - a1 < 0) {
			throw new IllegalArgumentException("b1 (" + b1 + ") - a1 (" + a1
					+ ") < 0");
		}
		if (b2 - a2 < 0) {
			throw new IllegalArgumentException("b2 (" + b2 + ") - a2 (" + a2
					+ ") < 0");
		}
		T[][] ret = Arrays.copyOf(array, b1 - a1);
		for (int i = 0; i < ret.length; i++) {
			if (0 <= a1 + i && a1 + i < array.length) {
				ret[i] = Arrays.copyOfRange(array[a1 + i], a2, b2);
			} else {
				ret[i] = Arrays.copyOfRange(array[0], a2, b2);
				Arrays.fill(ret[i], null);
			}
		}
		return ret;
	}
}
