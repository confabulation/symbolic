//
// Auto-generated file from float_/test/DOK1DfloatTest.java
//
package sparse.int_.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sparse.int_.DOK1Dint;

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
