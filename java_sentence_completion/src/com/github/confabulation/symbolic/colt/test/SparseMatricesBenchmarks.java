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

package com.github.confabulation.symbolic.colt.test;

import java.util.Random;

import com.github.confabulation.symbolic.sparse.float_.CSC2Dfloat;
import com.github.confabulation.symbolic.sparse.float_.CSR2Dfloat;
import com.github.confabulation.symbolic.sparse.float_.DOK1Dfloat;
import com.github.confabulation.symbolic.sparse.float_.DOK2Dfloat;
import com.github.confabulation.symbolic.sparse.float_.Matrix2Dfloat;
import com.github.confabulation.symbolic.utils.Stopwatch;
import cern.colt.list.tfloat.FloatArrayList;
import cern.colt.list.tlong.LongArrayList;
import cern.colt.map.tfloat.AbstractLongFloatMap;

import cern.colt.matrix.tfloat.impl.SparseCCMFloatMatrix2D;
import cern.colt.matrix.tfloat.impl.SparseFloatMatrix1D;
import cern.colt.matrix.tfloat.impl.SparseFloatMatrix2D;
import cern.colt.matrix.tfloat.impl.SparseRCFloatMatrix2D;
import cern.colt.matrix.tfloat.impl.SparseRCMFloatMatrix2D;

public class SparseMatricesBenchmarks {

	/**
	 * Benchmark conclusions: over 4 sizes (500, 1000, 2000, 10000), colt
	 * multiplication operations take O(size**2) while sparse custom takes less
	 * than a fourth of the time
	 */
	public static void main(String[] args) {
		// run_test();
		// run_test_custom_mult_v1_vs_v2();
		// run_test_DOK2D_vs_colt_custom();
//		run_test_colt_custom_vs_csr_vs_csc();
		run_test_csr_vs_csc();
	}

	public static void run_test() {
		int limit = 128000;

		for (int i = 1000; i <= limit; i *= 2) {
			System.out.println("> SIZE : " + i);
			test(1, i);
		}
		// compute intermediary points
		for (int i = 1500; i <= limit; i *= 2) {
			System.out.println("> SIZE : " + i);
			test(1, i);
		}
	}

	public static void test(int N, int size) {
		// parameters
		int matrix_fill = size;
		int vector_fill = size;

		// setup
		Stopwatch sw = new Stopwatch();

		SparseFloatMatrix2D sparse = new SparseFloatMatrix2D(size, size);

		Random rand = new Random();
		for (int i = 0; i < matrix_fill; i++) {
			sparse.setQuick(rand.nextInt(size), rand.nextInt(size),
					rand.nextFloat());
		}

		SparseFloatMatrix1D vector = new SparseFloatMatrix1D(size);

		if (vector_fill >= size) {
			for (int i = 0; i < vector.size(); i++) {
				vector.set(i, rand.nextFloat());
			}
		} else {
			for (int i = 0; i < vector_fill; i++) {
				vector.set(rand.nextInt(size), rand.nextFloat());
			}
		}

		// SparseCCFloatMatrix2D comp_col = new SparseCCFloatMatrix2D(size,
		// size);
		// comp_col.assign(sparse);

		SparseCCMFloatMatrix2D comp_col_mod = new SparseCCMFloatMatrix2D(size,
				size);
		comp_col_mod.assign(sparse);

		SparseRCFloatMatrix2D row_comp = new SparseRCFloatMatrix2D(size, size);
		row_comp.assign(sparse);

		SparseRCMFloatMatrix2D row_comp_mod = new SparseRCMFloatMatrix2D(size,
				size);
		row_comp_mod.assign(sparse);

		// tests
		sw.start("sparse custom");

		for (int i = 0; i < N; i++) {
			custom_mult(sparse, vector);
		}

		// sw.start("column compressed");
		// for (int i = 0; i < N; i++) {
		// comp_col.zMult(vector, null);
		// }

		sw.start("column compressed modified");
		for (int i = 0; i < N; i++) {
			comp_col_mod.zMult(vector, null);
		}

		sw.start("compressed row");
		for (int i = 0; i < N; i++) {
			row_comp.zMult(vector, null);
		}

		sw.start("compressed row modified");
		for (int i = 0; i < N; i++) {
			row_comp.zMult(vector, null);
		}

		sw.stop();

		System.out.println(sw.stats());
	}

	public static void run_test_custom_mult_v1_vs_v2() {
		int limit = 100000;
		for (int i = 10000; i <= limit; i *= 2) {
			System.out.println("> SIZE : " + i);
			test_custom_mult_v1_vs_v2(i);
		}
	}

	public static void test_custom_mult_v1_vs_v2(int size) {
		// parameters
		int matrix_fill = size;
		int vector_fill = size;

		// setup
		Stopwatch sw = new Stopwatch();

		SparseFloatMatrix2D sparse = new SparseFloatMatrix2D(size, size);

		Random rand = new Random();
		for (int i = 0; i < matrix_fill; i++) {
			sparse.setQuick(rand.nextInt(size), rand.nextInt(size),
					rand.nextFloat());
		}

		SparseFloatMatrix1D vector = new SparseFloatMatrix1D(size);

		if (vector_fill >= size) {
			for (int i = 0; i < vector.size(); i++) {
				vector.set(i, rand.nextFloat());
			}
		} else {
			for (int i = 0; i < vector_fill; i++) {
				vector.set(rand.nextInt(size), rand.nextFloat());
			}
		}

		sw.start("custom_mult v1");
		custom_mult(sparse, vector);

		sw.start("custom_mult v2");
		custom_mult_v2(sparse, vector);

		sw.stop();

		System.out.println(sw.stats());
	}

	public static SparseFloatMatrix1D custom_mult(SparseFloatMatrix2D A,
			SparseFloatMatrix1D b) {

		// get only non-zero elements from the excitations
		AbstractLongFloatMap element = b.elements();
		LongArrayList src_excitations_index = new LongArrayList();
		FloatArrayList src_excitations_value = new FloatArrayList();
		element.pairsSortedByKey(src_excitations_index, src_excitations_value);

		/*
		 * System.out.println("size : "+src_excitations_index.size());
		 * System.out.println("index new : "+(int)src_excitations_index.get(0));
		 * System.out.println("value new : "+(int)src_excitations_value.get(0));
		 */

		float link_strength = 0;
		SparseFloatMatrix1D ret = new SparseFloatMatrix1D(A.rows());

		for (int i = 0; i < A.rows(); i++) {
			for (int j = 0; j < src_excitations_index.size(); j++) {

				link_strength = A.get(i, (int) src_excitations_index.get(j));

				if (link_strength != 0) {
					// System.out.println("ajoute a l'index "+i+" : "+kb_get_index*src_excitations_value.get(j));
					ret.setQuick(i, ret.getQuick(i) + link_strength
							* src_excitations_value.getQuick(j));
				}
			}
		}
		return ret;
	}

	public static SparseFloatMatrix1D custom_mult_v2(SparseFloatMatrix2D A,
			SparseFloatMatrix1D b) {

		SparseFloatMatrix1D ret = new SparseFloatMatrix1D(A.rows());

		// get only non-zero elements from the excitations
		AbstractLongFloatMap element = b.elements();
		LongArrayList src_indexes = element.keys();
		FloatArrayList src_values = element.values();

		float link_strength = 0;

		for (int j = 0; j < element.size(); j++) {
			int col = (int) src_indexes.get(j);

			for (int i = 0; i < A.rows(); i++) {
				link_strength = A.get(i, col);

				if (link_strength != 0) {
					// System.out.println("ajoute a l'index "+i+" : "+kb_get_index*src_excitations_value.get(j));

					ret.setQuick(i, ret.getQuick(i) + link_strength
							* src_values.getQuick(j));
				}
			}
		}
		return ret;
	}

	// DOK2D_vs_colt_custom

	public static void run_test_DOK2D_vs_colt_custom() {
		int init = 10000;
		int step = 10000;
		int end = 50000;

		for (int i = init; i <= end; i += step) {
			System.out.println("> SIZE : " + i);
			test_DOK2D_vs_colt_custom(i);
		}
	}

	public static void test_DOK2D_vs_colt_custom(int size) {
		// parameters
		int matrix_fill = size;
		int vector_fill = size;

		// setup
		Stopwatch sw = new Stopwatch();

		SparseFloatMatrix2D sparse = new SparseFloatMatrix2D(size, size);
		Matrix2Dfloat dok = new DOK2Dfloat(size, size);

		Random rand = new Random();

		for (int i = 0; i < matrix_fill; i++) {

			float ran_float = rand.nextFloat();
			int ran_l = rand.nextInt(size);
			int ran_c = rand.nextInt(size);

			sparse.setQuick(ran_l, ran_c, ran_float);
			dok.set(ran_l, ran_c, ran_float);
		}

		SparseFloatMatrix1D vector = new SparseFloatMatrix1D(size);
		DOK1Dfloat vec = new DOK1Dfloat(size);

		if (vector_fill >= size) {
			for (int i = 0; i < vector.size(); i++) {

				float ran_float = rand.nextFloat();
				vector.set(i, ran_float);
				vec.set(i, ran_float);
			}
		} else {
			for (int i = 0; i < vector_fill; i++) {

				float ran_float = rand.nextFloat();
				int ran_l = rand.nextInt(size);

				vector.set(ran_l, ran_float);
				vec.set(ran_l, ran_float);
			}
		}

		sw.start("DOK");
		dok.multiply(vec);

		sw.start("sparse_custom");
		custom_mult_v2(sparse, vector);

		sw.stop();

		System.out.println(sw.stats());
	}

	// colt custom vs CSR vs CSC multiplications

	public static void run_test_colt_custom_vs_csr_vs_csc() {
		int init = 10000;
		int step = 10000;
		int end = 50000;

		for (int i = init; i <= end; i += step) {
			System.out.println("> SIZE : " + i);
			test_colt_custom_vs_csr_vs_csc(i);
		}
	}

	public static void test_colt_custom_vs_csr_vs_csc(int size) {
		// parameters
		int matrix_fill = size;
		int vector_fill = size;

		// setup
		Stopwatch sw = new Stopwatch();

		SparseFloatMatrix2D sparse = new SparseFloatMatrix2D(size, size);
		Matrix2Dfloat dok = new DOK2Dfloat(size, size);

		Random rand = new Random();

		for (int i = 0; i < matrix_fill; i++) {

			float ran_float = rand.nextFloat();
			int ran_l = rand.nextInt(size);
			int ran_c = rand.nextInt(size);

			sparse.setQuick(ran_l, ran_c, ran_float);
			dok.set(ran_l, ran_c, ran_float);
		}

		SparseFloatMatrix1D vector = new SparseFloatMatrix1D(size);
		DOK1Dfloat vec = new DOK1Dfloat(size);

		if (vector_fill >= size) {
			for (int i = 0; i < vector.size(); i++) {

				float ran_float = rand.nextFloat();
				vector.set(i, ran_float);
				vec.set(i, ran_float);
			}
		} else {
			for (int i = 0; i < vector_fill; i++) {

				float ran_float = rand.nextFloat();
				int ran_l = rand.nextInt(size);

				vector.set(ran_l, ran_float);
				vec.set(ran_l, ran_float);
			}
		}
		CSR2Dfloat csr = new CSR2Dfloat(dok);
		CSC2Dfloat csc = new CSC2Dfloat(dok);

		sw.start("CSR");
		csr.multiply(vec);

		sw.start("CSC");
		csc.multiply(vec);

		sw.start("sparse_custom");
		custom_mult(sparse, vector);

		sw.stop();

		System.out.println(sw.stats());
	}

	// CSR vs CSC multiplications

	public static void run_test_csr_vs_csc() {
		int init = 10000;
		int step = 10000;
		int end = 50000;

		for (int i = init; i <= end; i += step) {
			System.out.println("> SIZE : " + i);
			test_csr_vs_csc(i);
		}
	}

	public static void test_csr_vs_csc(int size) {
		// parameters
		double matrix_fill_proportion = 0.0001;
		int vector_fill = (int) (0.1* size);

		// setup
		Stopwatch sw = new Stopwatch();

		Matrix2Dfloat dok = new DOK2Dfloat(size, size);

		Random rand = new Random();

		for (long i = 0; i < size * (((long) size) * matrix_fill_proportion); i++) {

			boolean ok = false;
			while (!ok) {
				
				int ran_l = rand.nextInt(size);
				int ran_c = rand.nextInt(size);
				if (dok.get(ran_l, ran_c) == 0) {
					
					dok.set(ran_l, ran_c, rand.nextFloat());
					ok = true;
				}
			}
		}

		DOK1Dfloat vec = new DOK1Dfloat(size);

		if (vector_fill >= size) {
			for (int i = 0; i < vec.nlines(); i++) {

				vec.set(i, rand.nextFloat());
			}
		} else {
			for (int i = 0; i < vector_fill; i++) {
				
				boolean ok = false;
				while (!ok) {
					
					int ran_l = rand.nextInt(size);
					
					if (vec.get(ran_l) == 0){
						vec.set(ran_l, rand.nextFloat());
						ok = true;
					}
				}
			}
		}
		CSR2Dfloat csr = new CSR2Dfloat(dok);
		CSC2Dfloat csc = new CSC2Dfloat(dok);

		sw.start("CSR (one hundred runs)");
		for (int i = 0; i < 100; i++) {
			csr.multiply(vec);
		}

		sw.start("CSC (one hundred runs)");
		for (int i = 0; i < 100; i++) {
			csc.multiply(vec);
		}

		sw.stop();

		System.out.println(sw.stats());
	}
}
