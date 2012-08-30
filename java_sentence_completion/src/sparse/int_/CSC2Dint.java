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
// Auto-generated file from float_/CSC2Dfloat.java
//
package sparse.int_;

import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

import sparse.Pair;
import sparse.PairOfInts;

/**
 * Compressed sparse column matrix
 * 
 * @author bernard and cedric
 * 
 */
public class CSC2Dint extends Matrix2Dint {

	protected final int[] A;
	protected final int[] IA;
	protected final int[] JA;

	public CSC2Dint(Matrix2Dint m) {
		super(m.nlines, m.ncols);
		int nnz = m.nnz();
		A = new int[nnz];
		IA = new int[ncols + 1];
		JA = new int[nnz];

		// get the nz elements ordered by column
		TreeMap<PairOfInts, Integer> ordered_m = new TreeMap<PairOfInts, Integer>();
		for (Entry<Pair<Integer, Integer>, Integer> e : m.nz_elements()) {
			ordered_m.put(new PairOfInts(e.getKey()).reverse(), e.getValue());
		}

		int ind_a = 0;
		// data begins at 0 in A
		IA[0] = ind_a;
		int c = 0;

		for (PairOfInts reversed_coord : ordered_m.navigableKeySet()) {
			// advance in lines until the column of coord
			while (c != reversed_coord.first) { // reversed: column is first
				IA[c + 1] = ind_a;
				c++;
			}

			// insert element's value and line in A and JA
			A[ind_a] = ordered_m.get(reversed_coord);
			JA[ind_a] = reversed_coord.second;
			ind_a++;
		}
		// end of non-zero elements: fill remaining IA indexes
		for (; c < ncols; c++) {
			IA[c + 1] = ind_a;
		}

		// int ind_a = 0;
		//
		// // indicate that the non-zero values start at index zero of A
		// IA[0] = ind_a;
		//
		// for (int c = 0; c < m.ncols; c++) {
		// for (int l = 0; l < m.nlines; l++) {
		//
		// int v = m.get(l, c);
		// if (v != 0) {
		// A[ind_a] = v;
		// JA[ind_a] = l;
		// ind_a++;
		// }
		// }
		// // memorise the end of the non-zero elements of the column in A
		// IA[c + 1] = ind_a;
		// }
	}

	/**
	 * @throws UnsupportedOperationException
	 *             not support of modification, because supporting it would be
	 *             allowing non-sense
	 */
	@Override
	public void setQuick(int l, int c, int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * time complexity: O(log(NNZ))
	 */
	@Override
	public int getQuick(int l, int c) {
		// search the element that has line number l in row c
		int index = Arrays.binarySearch(JA, IA[c], IA[c + 1], l);

		if (index < 0) { // not present
			return 0;
		}
		return A[index];
	}

	@Override
	public int nnz() {
		return A.length;
	}

	/**
	 * time complexity: O(NNZ + NNZ_vec)
	 */
	@Override
	public DOK1Dint multiply(DOK1Dint vec) {
		DOK1Dint res = new DOK1Dint(nlines);
		for (Entry<Integer, Integer> e : vec.nz_elements()) {
			int c = e.getKey();
			for (int i = IA[c]; i < IA[c + 1]; i++) {
				int l = JA[i];
				res.set(l, e.getValue()*A[i] + res.get(l));
			}
		}
		return res;
	}

	@Override
	public Set<Entry<Pair<Integer, Integer>, Integer>> nz_elements() {
		// would be faster if found a way to use arrays directly
		HashSet<Entry<Pair<Integer, Integer>, Integer>> res = new HashSet<Entry<Pair<Integer, Integer>, Integer>>();
		for (int c = 0; c < ncols; c++) {
			for (int i = IA[c]; i < IA[c + 1]; i++) {
				res.add(new Pair<Pair<Integer, Integer>, Integer>(
						new Pair<Integer, Integer>(JA[i], c), // coordinates
						A[i])); // value
			}
		}
		return res;
	}
}
