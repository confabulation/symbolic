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
package confabulation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import confabulation.ConfabulationStub;
import confabulation.ForwardConfabulation;

/**
 * @author bernard and cedric
 * 
 */
public class ForwardConfabulationTest {

	private File f;
	private ConfabulationStub c;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		f = File.createTempFile("ForwardConfabulationTest", "txt");

		int n_modules = 3;
		String[] corpus = {
				// CORPUS
				"je suis bernard", //
				"je suis fatigué", //
				"tu es bernard", //
				"tu es jolie", //
		};

		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		try {
			for (String s : corpus) {
				w.write(s);
				w.newLine();
			}
		} finally {
			if (w != null) {
				w.close();
			}
		}

		c = new ForwardConfabulation(n_modules, f.getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		f.delete();
	}

	/**
	 * Test method for
	 * {@link confabulation.ForwardConfabulation#next_expectation(java.lang.String[], int)}
	 * .
	 */
	@Ignore
	@Test
	public void testNext_expectation() {
		// TODO
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link confabulation.ForwardConfabulation#next_word(java.lang.String[], int)}
	 * .
	 */
	@Test
	public void testNext_word() {
		assertEquals("suis", c.next_word("je".split(" "), -1));
		assertEquals("es", c.next_word("tu".split(" "), -1));

		assertEquals("fatigué", c.next_word("je suis".split(" "), -1));
		assertEquals("jolie", c.next_word("tu es".split(" "), -1));

		// correct answer is bernard because kb[0][2] has link tu->bernard and
		// kb[1][2] has link suis->bernard
		assertEquals("bernard", c.next_word("tu suis".split(" "), -1));

		assertEquals(null, c.next_word("je tu".split(" "), -1));

		assertEquals("fatigué", c.next_word("je".split(" "), 2));
		assertEquals("jolie", c.next_word("tu".split(" "), 2));
	}

}
