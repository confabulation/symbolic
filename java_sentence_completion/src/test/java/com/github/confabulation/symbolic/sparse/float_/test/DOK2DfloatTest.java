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

package com.github.confabulation.symbolic.sparse.float_.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.confabulation.symbolic.sparse.float_.DOK1Dfloat;
import com.github.confabulation.symbolic.sparse.float_.DOK2Dfloat;

public class DOK2DfloatTest {

	private DOK2Dfloat m;

	@Before
	public void setUp() throws Exception {
		m = new DOK2Dfloat(10, 10);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		m.set(0, 0, 1);
		m.set(9, 9, (float) 66.6);
		m.set(3, 7, (float) 6.9);

		assertEquals(1, m.get(0, 0), 0);
		assertEquals((float) 66.6, m.get(9, 9), 0.00001);
		assertEquals((float) 6.9, m.get(3, 7), 0.00001);
		assertEquals(0, m.get(5, 5), 0);

		m.set(3, 7, 0);
		assertEquals(0, m.get(3, 7), 0);
	}

	@Test
	public void testNnz() {
		m.set(0, 0, 1);
		m.set(9, 9, (float) 66.6);
		m.set(3, 7, (float) 6.9);
		m.set(3, 7, 0);
		assertEquals(2, m.nnz());
	}

	@Test
	public void testNz_elements() {
		m.set(0, 0, 1);
		m.set(9, 9, (float) 66.6);
		m.set(3, 7, (float) 6.9);
		m.set(3, 7, 0);
		assertEquals(m.nnz(), m.nz_elements().size());
	}

	@Test
	public void testMultiplyI() {
		int size = 10;
		m = new DOK2Dfloat(size, size);

		// identity matrix
		for (int i = 0; i < size; i++) {
			m.set(i, i, 1);
		}

		DOK1Dfloat vec = new DOK1Dfloat(size);
		vec.set(0, 1);
		vec.set(9, (float) 66.6);
		vec.set(3, (float) 6.9);
		vec.set(3, 0);

		assertEquals(vec, m.multiply(vec));
	}

	@Test
	public void testReclaim() {
		m.setQuick(0, 0, 1);
		assertEquals(1, m.getQuick(0, 0), 0);

		m.setQuick(0, 0, 0);
		assertEquals(1, m.nnz());

		m.reclaim();
		assertEquals(0, m.nnz());

	}
}
