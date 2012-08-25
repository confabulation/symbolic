/**
 * 
 */
package utils.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.MathTools;

/**
 * @author bernard
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
	 * Test method for {@link utils.MathTools#ramp_fun(int)}.
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
