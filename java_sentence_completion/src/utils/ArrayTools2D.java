/**
 * Copyright 2011-2012 Bernard Paulus and Cédric Snauwaert
 * 
 * Confabulation_Symbolic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Confabulation_Symbolic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Confabulation_Symbolic.  If not, see <http://www.gnu.org/licenses/>.
 */

package utils;

import java.util.Arrays;

/**
 * Tools for 2D arrays
 * 
 * @author bernard and cedric
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
