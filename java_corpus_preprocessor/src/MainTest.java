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

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 */

/**
 * @author bernard
 * 
 */
public class MainTest {

	/**
	 * executed before each test
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * executed after each test
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link Main#main(java.lang.String[])}.
	 */
	@Ignore
	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link Main#find_abbreviations(java.io.File)}.
	 */
	@Ignore
	@Test
	public void testFind_abbreviations() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link Main#find_potential_abbreviations(java.io.File)}.
	 */
	@Ignore
	@Test
	public void testFind_potential_abbreviations() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link Main#find_simple_regex_abbreviations(java.io.File)}.
	 * 
	 * @throws IOException
	 *             should no happen
	 */
	@Test
	public void testFind_simple_regex_abbreviations() throws IOException {
		File f = File.createTempFile("testFind_simple_regex_abbreviations"
				+ new Random().nextLong(), "tmp");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		String[][] abbreviations_results = new String[][] {
				{ "U.S.A.'", "U.S.A." }, { "i.e.", "i.e." },
				{ "F.u.c.k.", "F.u.c.k." }, { "127.0.0.1", "127.0.0.1" },
				{ "-B42.42E42", "42.42" } };
		for (String[] pair : abbreviations_results) {
			w.write(pair[0]);
			w.write("\n");
		}
		w.write("\n");
		// TODO: write counter examples
		w.write("Hello World. :)");
		w.close();
		Set<String> result = Main.find_simple_regex_abbreviations(f);
		Set<String> fails = new HashSet<String>();
		for (String[] abbrv : abbreviations_results) {
			if (! result.remove(abbrv[1])){
				fails.add(abbrv[1] + " not found from " + abbrv[0]);
			}
		}
		if (! result.isEmpty()){
			for (String str : result) {
				fails.add(str + " found but not in regexpr");
			}
		}
		if (!fails.isEmpty()){
			for (String str : fails) {
				System.out.println("errors:\n=======");
				System.out.println(str);
				fail("Some patterns were either not found or some were found while not supposed to");
			}
		}
		f.delete();
	}

	/**
	 * Test method for {@link Main#in(java.lang.Object, T[])}.
	 */
	@Ignore
	@Test
	public void testIn() {
		fail("Not yet implemented");
	}
}
