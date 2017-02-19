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
// Auto-generated file from float_/test/DOK1DfloatTest.java
//
package com.github.confabulation.symbolic.sparse.int_.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.confabulation.symbolic.sparse.int_.DOK1Dint;

public class DOK1DintTest {

	private DOK1Dint vec;

	@Before
	public void setUp() throws Exception {
		vec = new DOK1Dint(10);
	}

	@Test
	public void testGet() {
		vec.set(0, 1);
		assertEquals(1, vec.get(0), 0);

		vec.set(3, (int) 2.5);
		assertEquals((int) 2.5, vec.get(3), 0);
	}

	@Test
	public void testNnz() {
		vec.set(0, 1);
		vec.set(3, (int) 2.5);
		vec.set(9, 42);
		vec.set(3, 0);

		assertEquals(2, vec.nnz());
	}

	@Test
	public void testNz_elements() {
		vec.set(0, 1);
		vec.set(3, (int) 2.5);
		vec.set(9, 42);
		vec.set(3, 0);

		assertEquals(vec.nnz(), vec.nz_elements().size());
	}

	@Test
	public void testReclaim() {
		vec.setQuick(0, 1);
		assertEquals(1, vec.getQuick(0), 0);

		vec.setQuick(0, 0);
		assertEquals(1, vec.nnz());

		vec.reclaim();
		assertEquals(0, vec.nnz());

	}

}
