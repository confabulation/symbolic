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

package com.github.confabulation.symbolic.utils.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.confabulation.symbolic.utils.HashStandardTrie;

public class HashStandardTrieTest {

	private HashStandardTrie<Integer> t;

	@Before
	public void setUp() throws Exception {
		t = new HashStandardTrie<Integer>();
	}

	@Test
	public void testFind_all() {
		t.add(Arrays.asList(1, 2));
		t.add(Arrays.asList(1, 2, 3));
		t.add(Arrays.asList(1, 2, 3, 4));
		t.add(Arrays.asList(2, 3, 4));
		List<List<Integer>> all = t.find_all(Arrays.asList(1, 2, 3));
		assertEquals(2, all.size());
		assertTrue(all.contains(Arrays.asList(1, 2)));
		assertTrue(all.contains(Arrays.asList(1, 2, 3)));
	}

	@Test
	public void testFind_longest() {
		t.add(Arrays.asList(1, 2));
		t.add(Arrays.asList(1, 2, 3));
		t.add(Arrays.asList(1, 2, 3, 4, 5));
		t.add(Arrays.asList(2, 3, 4));
		List<Integer> all = t.find_longest(Arrays.asList(1, 2, 3, 4));

		assertEquals(all, Arrays.asList(1, 2, 3));
	}

}
