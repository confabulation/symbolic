//
// Auto-generated file from float_/test/DOK2DfloatTest.java
//
package sparse.int_.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sparse.int_.DOK1Dint;
import sparse.int_.DOK2Dint;

public class DOK2DintTest {

	private DOK2Dint m;

	@Before
	public void setUp() throws Exception {
		m = new DOK2Dint(10, 10);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		m.set(0, 0, 1);
		m.set(9, 9, (int) 66.6);
		m.set(3, 7, (int) 6.9);

		assertEquals(1, m.get(0, 0), 0);
		assertEquals((int) 66.6, m.get(9, 9), 0.00001);
		assertEquals((int) 6.9, m.get(3, 7), 0.00001);
		assertEquals(0, m.get(5, 5), 0);

		m.set(3, 7, 0);
		assertEquals(0, m.get(3, 7), 0);
	}

	@Test
	public void testNnz() {
		m.set(0, 0, 1);
		m.set(9, 9, (int) 66.6);
		m.set(3, 7, (int) 6.9);
		m.set(3, 7, 0);
		assertEquals(2, m.nnz());
	}

	@Test
	public void testNz_elements() {
		m.set(0, 0, 1);
		m.set(9, 9, (int) 66.6);
		m.set(3, 7, (int) 6.9);
		m.set(3, 7, 0);
		assertEquals(m.nnz(), m.nz_elements().size());
	}

	@Test
	public void testMultiplyI() {
		int size = 10;
		m = new DOK2Dint(size, size);

		// identity matrix
		for (int i = 0; i < size; i++) {
			m.set(i, i, 1);
		}

		DOK1Dint vec = new DOK1Dint(size);
		vec.set(0, 1);
		vec.set(9, (int) 66.6);
		vec.set(3, (int) 6.9);
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
