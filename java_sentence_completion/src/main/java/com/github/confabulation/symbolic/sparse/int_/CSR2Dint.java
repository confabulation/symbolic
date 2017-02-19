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
// Auto-generated file from float_/CSR2Dfloat.java
//
package com.github.confabulation.symbolic.sparse.int_;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.github.confabulation.symbolic.sparse.Pair;
import com.github.confabulation.symbolic.sparse.PairOfInts;

/**
 * Compressed sparse row matrix
 * 
 * @author bernard and cedric
 * 
 */
public class CSR2Dint extends Matrix2Dint {

	protected int[] A;
	protected int[] IA;
	protected int[] JA;

	public CSR2Dint(Matrix2Dint m) {
		super(m.nlines, m.ncols);
		int nnz = m.nnz();
		A = new int[nnz];
		IA = new int[nlines + 1];
		JA = new int[nnz];

		// get the nz elements ordered by line
		TreeMap<PairOfInts, Integer> ordered_m = new TreeMap<PairOfInts, Integer>();
		for (Entry<Pair<Integer, Integer>, Integer> e : m.nz_elements()) {
			ordered_m.put(new PairOfInts(e.getKey()), e.getValue());
		}

		int ind_a = 0;
		// data begins at 0 in A
		IA[0] = ind_a;
		int l = 0;

		for (PairOfInts coord : ordered_m.navigableKeySet()) {
			// advance in lines until the line of coord
			while (l != coord.first) {
				IA[l + 1] = ind_a;
				l++;
			}

			// insert element's value and column in A and JA
			A[ind_a] = ordered_m.get(coord);
			JA[ind_a] = coord.second;
			ind_a++;
		}
		// end of non-zero elements: fill remaining IA indexes
		for (; l < nlines; l++) {
			IA[l + 1] = ind_a;
		}

		// // data begins at 0 in A
		// IA[0] = ind_a;
		//
		// for (int l = 0; l < m.nlines(); l++) {
		// for (int c = 0; c < m.ncols(); c++) {
		// int val = m.get(l, c);
		// if (val != 0) {
		// A[ind_a] = val;
		// JA[ind_a] = c;
		// ind_a++;
		// }
		// }
		// // nz elems of next row begin at ind_a in A
		// IA[l + 1] = ind_a;
		// }
	}

	/**
	 * @throws UnsupportedOperationException
	 *             operation is not supported because it is too costly
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
		int index = Arrays.binarySearch(JA, IA[l], IA[l + 1], c);
		if (index < 0) { // not found
			return 0;
		}
		return A[index];
	}

	@Override
	public int nnz() {
		return A.length;
	}

	/**
	 * time complexity: O(nlines + NNZ)
	 */
	@Override
	public DOK1Dint multiply(DOK1Dint vec) {
		DOK1Dint res = new DOK1Dint(nlines);
		int row_sum;
		for (int l = 0; l < nlines; l++) {
			row_sum = 0;
			for (int i = IA[l]; i < IA[l + 1]; i++) {
				row_sum += A[i] * vec.get(JA[i]);
			}
			res.set(l, row_sum);
		}
		return res;
	}

	@Override
	public Set<Entry<Pair<Integer, Integer>, Integer>> nz_elements() {
		// would be faster if found a way to use arrays directly
		HashSet<Entry<Pair<Integer, Integer>, Integer>> res = new HashSet<Entry<Pair<Integer, Integer>, Integer>>();
		for (int l = 0; l < nlines; l++) {
			for (int i = IA[l]; i < IA[l + 1]; i++) {
				res.add(new Pair<Pair<Integer, Integer>, Integer>(
						new Pair<Integer, Integer>(l, JA[i]), // coordinates
						A[i])); // value
			}
		}
		return res;
	}

}
