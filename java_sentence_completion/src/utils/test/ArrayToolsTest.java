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

package utils.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.ArrayTools;

public class ArrayToolsTest {

	private Object a;
	private Object b;
	private Object[] array;

	@Before
	public void setUp() throws Exception {
		a = new Object();
		b = new Object();
		array = new Object[] { null, a, b, null, a };
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void number_equal() {
		assertEquals(2, ArrayTools.number_equal(a, array));
		assertEquals(0, ArrayTools.number_equal(null, new Object[0]));
		assertEquals(0, ArrayTools.number_equal(null, (Object[]) null));
	}

	@Test
	public void removeEvery() {
		assertArrayEquals(new Object[] { null, b, null },
				ArrayTools.removeEvery(a, array));
		// String[] fake = ArrayTools.removeEvery(null, (String[]) null);
		// assertArrayEquals(new String[]{}, fake);
	}

	@Test
	public void intersect() {
		Object[] test1 = new Object[] { null, a, b };
		Object[] test2 = new Object[] { a, b };
		Object[] expected = new Object[] { a, b };
		assertArrayEquals(expected, ArrayTools.intersect(test1, test2));
	}

	@Test
	public void enumerate_sublists() {
		Integer[] ints = new Integer[] { 1, 2, 3, 4 };
		assertArrayEquals(new Integer[][] { new Integer[] { 1, 2 },
				new Integer[] { 2, 3 }, new Integer[] { 3, 4 } },
				ArrayTools.enumerate_sublists(2, (Object[]) ints));
	}

	@Test
	public void join() {
		assertEquals("1 2 3", ArrayTools.join(" ", 1, 2, 3));
	}

	@Test
	public void enumerate_all_possibilities() {
		Integer[][] possibilities = new Integer[][] { new Integer[] { 0, 1 },
				new Integer[] { 2 }, new Integer[] { 3, 4 } };
		Integer[][] combinaisons = new Integer[][] { new Integer[] { 0, 2, 3 },
				new Integer[] { 0, 2, 4 }, new Integer[] { 1, 2, 3 },
				new Integer[] { 1, 2, 4 } };
		assertArrayEquals(combinaisons,
				ArrayTools.enumerate_all_possibilities(possibilities));

		possibilities[1] = new Integer[0];
		assertArrayEquals(new Integer[0][0],
				ArrayTools.enumerate_all_possibilities(possibilities));
	}

	@Test
	public void replace_each() {
		Integer[] in = new Integer[] { 1, 2, 3 };
		Integer[] elems = new Integer[] { 1, 3 };
		Integer[] by = new Integer[] { 4, 1 };
		Integer[] out = new Integer[] { 4, 2, 1 };
		assertArrayEquals(out, ArrayTools.replace_each(elems, by, in));
	}

	@Test
	public void replace_all_by() {
		Integer[] in = new Integer[] { 1, 2, 3 };
		Integer[] elems = new Integer[] { 1, 3 };
		Integer by = 0;
		Integer[] out = new Integer[] { 0, 2, 0 };
		assertArrayEquals(out, ArrayTools.replace_all_by(elems, by, in));
	}

	@Test
	public void zip() {
		Integer[][] expected = new Integer[][] { new Integer[] { 1, 4 },
				new Integer[] { 2, 5 }, new Integer[] { 3, 6 } };
		Integer[][] actual = ArrayTools.zip(new Integer[] { 1, 2, 3 },
				new Integer[] { 4, 5, 6 });
		assertArrayEquals(expected, actual);
	}

	@Test
	public void new_fill() {
		Integer[] expected;
		
		expected = new Integer[] { 1, 2, 3, 4 };
		assertArrayEquals(expected, ArrayTools.new_fill(4, 1, 2, 3, 4, 5));

		expected = new Integer[] { 1, 1, 2, 2, 3, 4 };
		assertArrayEquals(expected, ArrayTools.new_fill(6, 1, 2, 3, 4));

		expected = new Integer[] { 1, 1, 1, 2, 2, 3, 3 };
		assertArrayEquals(expected, ArrayTools.new_fill(7, 1, 2, 3));

		expected = new Integer[] { 1, 1, 1, 1, 2, 2, 2, 2 };
		assertArrayEquals(expected, ArrayTools.new_fill(8, 1, 2));

		expected = new Integer[] { null, null, null };
		assertArrayEquals(expected, ArrayTools.new_fill(3, new Integer[0]));
		
		assertArrayEquals(null, ArrayTools.new_fill(3, (Integer[]) null));
	}
	
	@Test
	public void concat() {
		Integer[] expected1 = {1, 2, 3};
		assertArrayEquals(expected1, ArrayTools.concat(expected1, null, null));
		
		Integer[] expected2 = {1, 2, 3, 1, 2, 3, 1, 2, 3};
		assertArrayEquals(expected2, ArrayTools.concat(expected1, expected1, expected1));
	}
	
	@Test
	public void get_correspnding_values() {
		Integer[] keys = {1, 2, 3, 2};
		Integer[] values = {0, 1, 2, 3};
		
		Integer[] expected1 = {1, 3};
		assertArrayEquals(expected1, ArrayTools.get_map(2, keys, values));
		
		Integer[] expected2 = {};
		assertArrayEquals(expected2, ArrayTools.get_map(0, keys, values));
	}
	
	@Test
	public void copy_indexes_fill_others() {
		Integer[] array = {0, 1, 2, 3};
		int[] indexes = {1, 3};
		Integer fill = null;
		
		Integer[] result = ArrayTools.copy_indexes_fill_others(fill, indexes, array);
		
		// test according to specification
		assertEquals(array.length, result.length);
		for (int i = 0; i < result.length; i++) {
			
			if (ArrayTools.in(i, indexes)){
				assertEquals(array[i], result[i]);
			} else {
				assertEquals(fill, result[i]);
			}
		}
	}
	
	@Test
	public void all_equals() {
		int[] test1 = {1, 2};
		assertFalse(ArrayTools.all_equals(1, test1));
		
		int[] test2 = {1, 1, 1};
		assertTrue(ArrayTools.all_equals(1, test2));
	}
	
	@Test
	public void range() {
		int[] expected1 = {1, 2, 3};
		assertArrayEquals(expected1, ArrayTools.range(1, 4, 1));
		
		int[] expected2 = {-10, -8, -6};
		assertArrayEquals(expected2, ArrayTools.range(-10, -4, 2));
		
		int[] expected3 = {};
		assertArrayEquals(expected3, ArrayTools.range(-10, -4, -2));
		
		int[] expected4 = {4, 2, 0, -2, -4};
		assertArrayEquals(expected4, ArrayTools.range(4, -6, -2));
	}

}
