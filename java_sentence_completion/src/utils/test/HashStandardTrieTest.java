package utils.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.HashStandardTrie;

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
