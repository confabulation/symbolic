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
// Auto-generated file from float_/test/CSR2DfloatTest.java
//
package sparse.int_.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import sparse.int_.CSR2Dint;
import sparse.int_.DOK1Dint;
import sparse.int_.DOK2Dint;
import sparse.int_.Matrix2Dint;

public class CSR2DintTest {
	
	private CSR2Dint m;
	private DOK2Dint dok;

	@Before
	public void setUp() throws Exception {
		int size = 10;
		dok = new DOK2Dint(size, size);
		
		// identity matrix
		for (int i = 0; i < size; i++) {
			dok.set(i, i, 1);
		}
		
		this.m = new CSR2Dint(dok);
	}
	
	@Test
	public void testGet() {
		assertEquals(1, m.get(1, 1), 0);

		DOK2Dint dok = new DOK2Dint(10, 10);
		// first line == [0 1 2 3 4 5 ... ]
		for (int c = 0; c < dok.ncols(); c++) {
			dok.set(0, c, c);
		}
		// first col == [0 1.5 2.5 3.5 ...]
		for (int l = 1; l < dok.nlines(); l++) {
			dok.set(l, 0, (int) (l + 0.5));
		}

		Matrix2Dint m = new CSR2Dint(dok);
		for (int l = 0; l < m.nlines(); l++) {
			for (int c = 0; c < m.ncols(); c++) {
				// print line and column number on error
				assertArrayEquals(
						new Integer[] { (int) l, (int) c, dok.get(l, c) },
						new Integer[] { (int) l, (int) c, m.get(l, c) });
			}

		}
	}

	@Test
	public void testMultiplyI() {
		DOK1Dint vec = new DOK1Dint(m.ncols());
		vec.set(0, 1);
		vec.set(9, (int) 66.6);
		vec.set(3, (int) 6.9);
		vec.set(3, 0);
		
		assertEquals(vec, m.multiply(vec));
	}
	
	@Test
	public void testNz_elements() {
		assertEquals(m.nnz(), m.nz_elements().size());
	}
	
	@Test
	public void test_constructor(){
		for (int l = 0; l < m.nlines(); l++) {
			for (int c = 0; c < m.ncols(); c++) {
				assertArrayEquals(
						new Integer[] { (int) l, (int) c, dok.get(l, c) },
						new Integer[] { (int) l, (int) c, m.get(l, c) });
			}

		}
	}
	
	@Test
	public void testMultiplyRandom() {
		Random r = new Random();
		DOK2Dint dok = new DOK2Dint(10 + r.nextInt(10), 10 + r.nextInt(10));
		
		// random matrix
		for (int l = 0; l < dok.nlines(); l++) {
			for (int c = 0; c < dok.ncols(); c++) {
				// 50% chance of random element
				if (r.nextBoolean()){
					dok.set(l, c, r.nextInt());
				}
			}
		}
		
		
		DOK1Dint vec = new DOK1Dint(dok.ncols());
		
		// random vec
		for (int l = 0; l < vec.nlines(); l++) {
			if (r.nextBoolean()){
				vec.set(l, r.nextInt());
			}
		}
		
		// expected result
		DOK1Dint expected = dok.multiply(vec);
		
		assertEquals(expected, new CSR2Dint(dok).multiply(vec));
	}

}
