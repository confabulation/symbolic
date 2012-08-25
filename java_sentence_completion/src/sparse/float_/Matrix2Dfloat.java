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

package sparse.float_;

import java.util.Map.Entry;
import java.util.Set;

import sparse.Pair;

public abstract class Matrix2Dfloat {

	protected int nlines;
	protected int ncols;

	public Matrix2Dfloat(int nlines, int ncols) {
		this.nlines = nlines;
		this.ncols = ncols;
	}

	/**
	 * sets element at (l, c) to value
	 * <p>
	 * might throw {@link UnsupportedOperationException} when modifications are
	 * not supported
	 * </p>
	 * 
	 * @param l
	 *            the line number, in [0, nlines[
	 * @param c
	 *            the column number, in [0, ncols[
	 * @param value
	 *            the value to be set
	 */
	public void set(int l, int c, float value) {
		check_bounds(l, c);
		setQuick(l, c, value);
	}

	/**
	 * sets element at (l, c) to value with the fewest checks possible
	 * <p>
	 * see documentation in used subclass before use
	 * </p>
	 * w
	 * <p>
	 * might throw {@link UnsupportedOperationException}
	 * </p>
	 */
	public abstract void setQuick(int l, int c, float value);

	/**
	 * the element at line l and column c
	 * 
	 * @param l
	 *            the line number, in [0, nlines[
	 * @param c
	 *            the column number, in [0, ncols[
	 * @return the corresponding element
	 */
	public float get(int l, int c) {
		check_bounds(l, c);
		return getQuick(l, c);
	}

	/**
	 * the element at line l and column c
	 * <p>
	 * access is done with the fewest checks possible. For instance: no bound
	 * checking
	 * </p>
	 */
	public abstract float getQuick(int l, int c);

	/**
	 * @return the nlines
	 */
	public int nlines() {
		return nlines;
	}

	/**
	 * @return the ncols
	 */
	public int ncols() {
		return ncols;
	}

	/**
	 * number of non-zero elements of the matrix
	 * 
	 * @return the number of non-zero elements
	 */
	public abstract int nnz();

	/**
	 * checks if l and c are in bounds
	 * 
	 * @param l
	 *            line number
	 * @param c
	 *            column number
	 * @throws IndexOutOfBoundsException
	 *             if l or c is not in the matrix bounds
	 */
	protected void check_bounds(int l, int c) throws IndexOutOfBoundsException {
		// bounds
		if (l >= nlines || c >= ncols) {
			throw new IndexOutOfBoundsException(l + " >= " + nlines + " or "
					+ c + " >= " + ncols);
		}
		if (l < 0 || c < 0) {
			throw new IndexOutOfBoundsException("l (" + l + ") < 0 or c (" + c
					+ ") <0");
		}
	}

	/**
	 * Multiplies the matrix by the given vector
	 * 
	 * @param vec
	 *            non-null
	 * @return the result in a new vector
	 */
	public abstract DOK1Dfloat multiply(DOK1Dfloat vec);

	/**
	 * returns a set of the non-zero elements
	 * 
	 * @return the non-zero elements
	 */
	public abstract Set<Entry<Pair<Integer, Integer>, Float>> nz_elements();

	@Override
	public String toString() {
		String str = "Matrix [nlines=" + nlines + ", ncols=" + ncols + "]\n";
		for (int l = 0; l < nlines; l++) {
			for (int c = 0; c < ncols; c++) {
				str += get(l, c) + " ";
			}
			str += "\n";
		}
		return str;
	}
}