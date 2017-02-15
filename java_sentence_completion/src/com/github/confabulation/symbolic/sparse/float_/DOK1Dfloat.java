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

package com.github.confabulation.symbolic.sparse.float_;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

/**
 * DOK vector
 * 
 * @author bernard and cedric
 * 
 */
public class DOK1Dfloat {
	protected final int nlines;
	protected HashMap<Integer, Float> d;

	/**
	 * creates a new dok vector
	 * 
	 * @param nlines
	 *            the size of the vector > 0
	 */
	public DOK1Dfloat(int nlines) {
		d = new HashMap<Integer, Float>();
		if (nlines < 0) {
			throw new IllegalArgumentException("nlines (" + nlines
					+ "must be positive");
		}
		this.nlines = nlines;
	}
	
	/**
	 * copy constructor
	 * @param m non-null
	 */
	public DOK1Dfloat(DOK1Dfloat m){
		this(m.nlines());
		for (Entry<Integer, Float> e: m.nz_elements()) {
			set(e.getKey(), e.getValue());
		}
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
	public void set(int l, float value) {
		check_bounds(l);
		// do not store zero values
		if (value == 0) {
			Integer l_boxed = l;
			if (d.containsKey(l_boxed)) {
				d.remove(l_boxed);
			}
		} else {
			setQuick(l, value);
		}
	}

	/**
	 * sets element at line l, without checks nor memory management
	 * <p>
	 * if value inserted is 0, it will consume memory
	 * </p>
	 * <p>
	 * if this call zeroes a previously non-null element, the memory will not be
	 * freed
	 * </p>
	 * @see #reclaim()
	 */
	public void setQuick(int l, float value) {
		d.put(l, value);
	}

	/**
	 * the element at line l and column c
	 * 
	 * @param l
	 *            the line number, in [0, nlines[
	 * @param c
	 *            the column number, in [0, ncols[
	 * @return the corresponding element
	 */
	public float get(int l) {
		check_bounds(l);
		return getQuick(l);
	}

	/**
	 * get the element at line l, without checks
	 */
	public float getQuick(int l) {
		Float val = d.get(l);
		if (val == null) { // not present
			return 0;
		}
		return val;
	}

	/**
	 * reclaims memory that was not properly freed when using
	 * {@link #setQuick(int, float)}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if an element was added outside the bounds
	 */
	public void reclaim() throws IndexOutOfBoundsException {
		// list first, since modifying the map while iterating over it's
		// elements has undefined results
		LinkedList<Integer> list = new LinkedList<Integer>();

		for (Entry<Integer, Float> p : d.entrySet()) {

			float value = p.getValue();
			Integer l = p.getKey();
			if (value == 0) {
				list.add(l);
			}

			// die if outside the bounds
			check_bounds(l);
		}

		// remove all
		for (Integer coord : list) {
			d.remove(coord);
		}
	}

	/**
	 * number of non-zero elements of the matrix
	 * 
	 * @return the number of non-zero elements
	 */
	public int nnz() {
		return d.size();
	}

	/**
	 * check if l is in bounds
	 * 
	 * @param l
	 *            line number
	 * @throws IndexOutOfBoundsException
	 *             if l is outside the vector bounds
	 */
	protected void check_bounds(int l) throws IndexOutOfBoundsException {
		if (l < 0 || l >= nlines) {
			throw new IndexOutOfBoundsException(l + " < 0 or " + l + " >= "
					+ nlines);
		}
	}

	/**
	 * returns a set of the non-zero elements
	 * <p>
	 * Note: this only returns the elements in memory: if the unsafe
	 * {@link #setQuick(int, float)} was used an no {@link #reclaim()} has been
	 * done since, this can contain zero values
	 * </p>
	 * 
	 * @return the non-zero elements
	 */
	public Set<Entry<Integer, Float>> nz_elements() {
		return d.entrySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + nlines;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		DOK1Dfloat other = (DOK1Dfloat) obj;
		if (nlines != other.nlines) {
			return false;
		}
		if (!nz_elements().equals(other.nz_elements())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DOK1Dfloat [nlines=" + nlines + ", d=" + d + "]";
	}

	/**
	 * size of the vector
	 * 
	 * @return number of line of the vector
	 */
	public int nlines() {
		return nlines;
	}

	/**
	 * Sums each element of other with each element of this array
	 * 
	 * @param other
	 *            a vector, non-null, correctly memory-managed
	 * @see #reclaim()
	 * @see #set(int, float)
	 * @see #setQuick(int, float)
	 */
	public void add(DOK1Dfloat other) {
		for (Entry<Integer, Float> e : other.nz_elements()) {
			int l = e.getKey();
			set(l, e.getValue() + get(l));
		}
	}

}
