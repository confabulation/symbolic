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

/**
 * 
 */
package com.github.confabulation.symbolic.utils.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.confabulation.symbolic.utils.MathTools;

/**
 * @author bernard and cedric
 * 
 */
public class MathToolsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.github.confabulation.symbolic.utils.MathTools#ramp_fun(int)}.
	 */
	@Test
	public void ramp() {
		assertEquals(0, MathTools.ramp_fun(0));
		assertEquals(0, MathTools.ramp_fun(-1));
		assertEquals(1, MathTools.ramp_fun(1));

		assertEquals(0, MathTools.ramp_fun((float) 0), 0.000001);
		assertEquals(0, MathTools.ramp_fun((float) -1), 0.000001);
		assertEquals(1, MathTools.ramp_fun((float) 1), 0.000001);
	}

	@Test
	public void sum() {
		assertEquals(-2, MathTools.sum(1, -5, 2));
		assertEquals(0, MathTools.sum(new int[] {}));
	}

	@Test
	public void min() {
		assertEquals(-1, MathTools.min(0, 2, -1, 3));
	}

	@Test
	public void max() {
		assertEquals(3, MathTools.max(0, 2, -1, 3));
	}

}
