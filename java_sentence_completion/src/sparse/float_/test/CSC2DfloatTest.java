package sparse.float_.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import sparse.float_.CSC2Dfloat;
import sparse.float_.DOK1Dfloat;
import sparse.float_.DOK2Dfloat;
import sparse.float_.Matrix2Dfloat;

public class CSC2DfloatTest {

	private CSC2Dfloat m;
	private DOK2Dfloat dok;

	@Before
	public void setUp() throws Exception {
		int size = 10;
		dok = new DOK2Dfloat(size, size);

		// identity matrix
		for (int i = 0; i < size; i++) {
			dok.set(i, i, 1);
		}

		this.m = new CSC2Dfloat(dok);
	}

	@Test
	public void testGet() {
		assertEquals(1, m.get(1, 1), 0);

		DOK2Dfloat dok = new DOK2Dfloat(10, 10);
		// first line == [0 1 2 3 4 5 ... ]
		for (int c = 0; c < dok.ncols(); c++) {
			dok.set(0, c, c);
		}
		// first col == [0 1.5 2.5 3.5 ...]
		for (int l = 1; l < dok.nlines(); l++) {
			dok.set(l, 0, (float)(l + 0.5));
		}

		Matrix2Dfloat m = new CSC2Dfloat(dok);
		for (int l = 0; l < m.nlines(); l++) {
			for (int c = 0; c < m.ncols(); c++) {
				// print line and column number on error
				assertArrayEquals(
						new Float[] { (float) l, (float) c, dok.get(l, c) },
						new Float[] { (float) l, (float) c, m.get(l, c) });
			}

		}
	}

	@Test
	public void testMultiplyI() {
		DOK1Dfloat vec = new DOK1Dfloat(m.ncols());
		vec.set(0, 1);
		vec.set(9, (float) 66.6);
		vec.set(3, (float) 6.9);
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
						new Float[] { (float) l, (float) c, dok.get(l, c) },
						new Float[] { (float) l, (float) c, m.get(l, c) });
			}

		}
	}
	
	@Test
	public void testMultiplyRandom() {
		Random r = new Random();
		DOK2Dfloat dok = new DOK2Dfloat(10 + r.nextInt(10), 10 + r.nextInt(10));
		
		// random matrix
		for (int l = 0; l < dok.nlines(); l++) {
			for (int c = 0; c < dok.ncols(); c++) {
				// 50% chance of random element
				if (r.nextBoolean()){
					dok.set(l, c, r.nextFloat());
				}
			}
		}
		
		
		DOK1Dfloat vec = new DOK1Dfloat(dok.ncols());
		
		// random vec
		for (int l = 0; l < vec.nlines(); l++) {
			if (r.nextBoolean()){
				vec.set(l, r.nextFloat());
			}
		}
		
		// expected result
		DOK1Dfloat expected = dok.multiply(vec);
		
		assertEquals(expected, new CSC2Dfloat(dok).multiply(vec));
	}

}
