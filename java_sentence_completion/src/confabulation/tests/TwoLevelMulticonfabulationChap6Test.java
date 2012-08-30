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

package confabulation.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import parser.Tokenizer;
import parser.WordTokenizer;

import utils.ArrayTools;

import confabulation.Module;
import confabulation.TwoLevelMulticonfabulationChap6;

/**
 * @author bernard and cedric
 * 
 */
public class TwoLevelMulticonfabulationChap6Test extends
		TwoLevelMulticonfabulationChap6 {

	private static File f;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] corpus = {
				//
				"the wookies won the war", //
				"you must feel the force", //
				"the force is with you", //
		};
		f = ConfabulationStubTest.temp_file(
				"TwoLevelMulticonfabulationChap6Test", corpus);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		f.delete();
	}

	public TwoLevelMulticonfabulationChap6Test() throws IOException {
		super(10, f.getAbsolutePath(), 3);
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
		clean();
	}

	/**
	 * Test method for
	 * {@link confabulation.TwoLevelMulticonfabulationChap6#next_word(java.lang.String[], int)}
	 * .
	 */
	@Test
	public void testNext_word() {
		Tokenizer tok = new WordTokenizer();
		String[] sentences = { "the","the wookies", "the force", "you"};
		String[] expecteds = { "wookies", "won" , "is", "must"};

		String[][] inputs = new String[sentences.length][];
		for (int i = 0; i < sentences.length; i++) {
			inputs[i] = tok.parse(sentences[i], " ");
		}

		for (int i = 0; i < inputs.length; i++) {
			String res = next_word(inputs[i], -1);
			assertEquals("input: " + Arrays.toString(inputs[i]), expecteds[i],
					res);
		}
	}

	/**
	 * Test method for
	 * {@link confabulation.TwoLevelMulticonfabulationChap6#after_swirls(confabulation.Module[], confabulation.KnowledgeBase[][], int, confabulation.Module)}
	 * .
	 */
	@Test
	public void testAfter_swirls() {
		String[][] inputs = { { null, "wookies", "won", "the", "war" } };
		String[] expecteds = { "wookies" };

		for (int i = 0; i < inputs.length; i++) {
			activate(inputs[i], w);

			int p_index = ArrayTools.find_not_equal(null, inputs[i]);
			Module[] w_modules = ArrayTools.copy_indexes_fill_others(null,
					ArrayTools.find_not_equals(null, inputs[i]), w);
			String res = after_swirls(w_modules, w_to_p, p_index, p[p_index]);

			assertEquals(expecteds[i], res);

			clean();
		}

	}

}
