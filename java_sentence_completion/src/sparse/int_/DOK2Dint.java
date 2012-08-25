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

//
// Auto-generated file from float_/DOK2Dfloat.java
//
package sparse.int_;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import sparse.Pair;

/**
 * Dictionnary of keys sparse matrix
 * 
 * @author bernard
 * 
 */
public class DOK2Dint extends Matrix2Dint {

	/**
	 * dictionary (line, column) -> value
	 */
	protected HashMap<Pair<Integer, Integer>, Integer> d;

	public DOK2Dint(int nlines, int ncols) {
		super(nlines, ncols);
		d = new HashMap<Pair<Integer, Integer>, Integer>();
	}
	
	/**
	 * copy constructor
	 * @param m non-null
	 */
	public DOK2Dint(Matrix2Dint m){
		this(m.nlines(), m.ncols());
		for (Entry<Pair<Integer, Integer>, Integer> e : m.nz_elements()) {
			set(e.getKey().first, e.getKey().second, e.getValue());
		}
	}

	/**
	 * the element at line l and column c
	 * <p>
	 * This does not perform bound checking
	 * </p>
	 * 
	 * @param l
	 *            the line number, in [0, nlines[
	 * @param c
	 *            the column number, in [0, ncols[
	 * @return the corresponding element
	 */
	@Override
	public int getQuick(int l, int c) {
		Pair<Integer, Integer> coord = new Pair<Integer, Integer>(l, c);
		if (d.containsKey(coord)) {
			return d.get(coord);
		}
		return 0;
	}

	public void set(int l, int c, int value) {
		check_bounds(l, c);
		if (value == 0) {
			Pair<Integer, Integer> coord = new Pair<Integer, Integer>(l, c);
			if (d.containsKey(coord)) {
				d.remove(coord);
			}

		} else {
			setQuick(l, c, value);
		}
	}

	/**
	 * sets element at (l, c) to value
	 * <p>
	 * does not perform memory management, nor bound checking
	 * <p>
	 * 
	 * @param l
	 *            the line number, in [0, nlines[
	 * @param c
	 *            the column number, in [0, ncols[
	 * @param value
	 *            the value to be set
	 * @see #reclaim()
	 */
	@Override
	public void setQuick(int l, int c, int value) {

		d.put(new Pair<Integer, Integer>(l, c), value);

	}

	/**
	 * reclaims memory that was not properly freed when using
	 * {@link #setQuick(int, int, int)}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if an element was added outside the bounds
	 */
	public void reclaim() throws IndexOutOfBoundsException {
		// list first, since modifying the map while iterating over it's
		// elements has undefined results
		LinkedList<Pair<Integer, Integer>> list = new LinkedList<Pair<Integer, Integer>>();

		for (Entry<Pair<Integer, Integer>, Integer> p : d.entrySet()) {

			int value = p.getValue();
			Pair<Integer, Integer> coord = p.getKey();
			if (value == 0) {
				list.add(coord);
			}

			// die if value is outside bounds
			check_bounds(coord.first, coord.second);
		}

		// remove all
		for (Pair<Integer, Integer> coord : list) {
			d.remove(coord);
		}
	}

	@Override
	public int nnz() {
		return d.size();
	}

	/**
	 * returns a set of the non-zero elements
	 * <p>
	 * Note: this only returns the elements in memory: if the unsafe
	 * {@link #setQuick(int, int)} was used an no {@link #reclaim()} has been
	 * done since, this can contain zero values
	 * </p>
	 * 
	 * @return the non-zero elements
	 */
	@Override
	public Set<Entry<Pair<Integer, Integer>, Integer>> nz_elements() {
		return d.entrySet();
	}

	/**
	 * time complexity: O(nlines * NNZ_vec)
	 */
	@Override
	public DOK1Dint multiply(DOK1Dint vec) {
		// get only non-zero elements from the excitations
		Set<Entry<Integer, Integer>> element = vec.nz_elements();

		int link_strength = 0;
		DOK1Dint ret = new DOK1Dint(nlines);

		for (Entry<Integer, Integer> e : element) {
			int col = e.getKey();

			for (int l = 0; l < nlines; l++) {

				link_strength = getQuick(l, col);
				if (link_strength != 0) {
					ret.setQuick(l,
							ret.getQuick(l) + link_strength * e.getValue());
				}
			}
		}
		ret.reclaim();
		return ret;
	}
}
