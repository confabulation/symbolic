package confabulation.tests;

import static confabulation.tests.ConfabulationStubTest.assertArrayEqualsDBG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import parser.WordTokenizer;

import utils.ArrayTools;

import confabulation.KnowledgeBase;
import confabulation.Module;
import confabulation.SymbolMapping;
import confabulation.TwoLevelSimpleConfabulation;

public class TwoLevelSimpleConfabulationTest extends
		TwoLevelSimpleConfabulation {

	private static File f;
	private static final int n_modules = 3;

	public TwoLevelSimpleConfabulationTest() throws IOException {
		super(n_modules, f.getAbsolutePath());
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		String[] corpus = {
				// CORPUS
				"I like pizza", //
				"I like cookies", //
				"I eat pizza" //
		};
		f = ConfabulationStubTest.temp_file("TwoLevelSimpleConfabulationTest",
				corpus);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		f.delete();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		clean();
	}

	@Test
	public void testNext_word() {
		String[] symbols = { "I", "eat" };
		String answer = next_word(symbols, -1);
		assertEquals("pizza", answer);

		// this did not work previously:
		// the word module first receives input from "bernard" and "fatigue"
		// since both excitations are below B, C1F removes them.
		// this is due to the fact that we define CKF as
		// "keep all the excitations above K* B"
		// while the book (CT:TMT, p. 141) defines it as
		// "confabulation conclusions having K or more knowledge link inputs"
		// 
		// => defined new semantic with kbase input count for modules

		String[] symbols2 = { "I" };
		String answer2 = next_word(symbols2, -1);
		assertEquals("eat", answer2);

		String[] symbols1 = { "I", "like" };
		String answer1 = next_word(symbols1, -1);
		assertEquals("cookies", answer1);
	}

	/**
	 * Demonstrates a failure of Robert Hecht Nielsen algorithm
	 */
	@Test
	public void next_word2() throws IOException {
		String[] corpus = {
				// CORPUS
				"a b c", //
				"b b c", //
				"c b d", //
		};
		File f = ConfabulationStubTest.temp_file("next_word2", corpus);
		try {
			TwoLevelSimpleConfabulation tsc = new TwoLevelSimpleConfabulation(
					3, f.getAbsolutePath());
			String[] arg = "a b".split(" ");
			String actual = tsc.next_word(arg, -1);
			String expected;
			if ("d".equals(actual)) {
				expected = "d";
				System.err.println("Correct implementation, "
						+ "but failure of the algorithm of CT:TMT chap 7.3");
			} else {
				expected = "c"; // the actual correct answer, if the algo was
								// better
			}
			assertEquals(expected, actual);
		} finally {
			f.delete();
		}
	}

	@Test
	public void testTwoLevelSimpleConfabulation() {
		assertEquals(n_modules * 2, mods.length);
		for (Module mod : mods) {
			assertNotNull(mod);
		}
		assertArrayDimensions(kbs, n_modules * 2, n_modules * 2);
		assertArrayDimensions(w_to_w, n_modules, n_modules);
		assertArrayDimensions(w_to_p, n_modules, n_modules);
		assertArrayDimensions(p_to_w, n_modules, n_modules);
		assertArrayDimensions(p_to_p, n_modules, n_modules);

		// w_to_w: each module is linked with the ones after
		// want upper triangle (excluding diagonal) set
		for (int src = 0; src < w_to_w.length; src++) {
			for (int targ = 0; targ < w_to_w[src].length; targ++) {
				if (src < targ) {
					assertNotNull("(" + src + "," + targ + ")",
							w_to_w[src][targ]);
				} else {
					assertNull("(" + src + "," + targ + ")", w_to_w[src][targ]);
				}
			}
		}

		// p_to_p: each module is linked with the ones after
		// want upper triangle (excluding diagonal) set
		for (int src = 0; src < p_to_p.length; src++) {
			for (int targ = 0; targ < p_to_p[src].length; targ++) {
				if (src < targ) {
					assertNotNull("(" + src + "," + targ + ")",
							p_to_p[src][targ]);
				} else {
					assertNull("(" + src + "," + targ + ")", p_to_p[src][targ]);
				}
			}
		}

		// w_to_p: each module is linked with the P module above or before
		// want lower triangle (including diagonal) set
		for (int src = 0; src < w_to_p.length; src++) {
			for (int targ = 0; targ < w_to_p[src].length; targ++) {
				if (src >= targ) {
					assertNotNull("(" + src + "," + targ + ")",
							w_to_p[src][targ]);
				} else {
					assertNull("(" + src + "," + targ + ")", w_to_p[src][targ]);
				}
			}
		}

		// p_to_w: each module is linked with the W module below or after
		// want upper triangle (including diagonal) set
		for (int src = 0; src < p_to_w.length; src++) {
			for (int targ = 0; targ < p_to_w[src].length; targ++) {
				if (src <= targ) {
					assertNotNull("(" + src + "," + targ + ")",
							p_to_w[src][targ]);
				} else {
					assertNull("(" + src + "," + targ + ")", p_to_w[src][targ]);
				}
			}
		}
	}

	public static <T> void assertArrayDimensions(T[][] array, int l1, int l2) {
		assertEquals(l1, array.length);
		for (int i = 0; i < array.length; i++) {
			assertEquals("array[" + i + "]", l2, array[i].length);
		}
	}

	@Test
	public void testConservative_rerepr() {
		
		String[][] symbolss = { { "I", "like" }, { "I", "eat" },
				{ "I" } };

		String[][][] expectedss = {
				//
				{ { syc.string_symbol(symbolss[0]) }, {} }, // symbols[0]
				ArrayTools.zip(symbolss[1]), // symbols[1]
				{ { "I", syc.string_symbol(symbolss[0]) } } // symbols[2]
		};

		for (int i = 0; i < symbolss.length; i++) {
			String[] symbols = symbolss[i];
			activate(symbols, w);

			conservative_rerepr(w, p, w_to_p, syc);

			for (int j = 0; j < symbols.length; j++) {
				assertArrayEqualsDBG(
						"symbols(" + i + "): " + Arrays.toString(symbols),
						expectedss[i][j], p[j].get_expectation());
			}

			clean();
		}
	}

	@Test
	public void testMultiword_symbol_treatment() {
		String[] vocab = { syc.string_symbol("orange", "marmalade"), "Ma-Ri" };
		SymbolMapping sm = ConfabulationStubTest.new_sm(vocab);
		Module[] mods = new Module[] { new Module(sm), new Module(sm) };

		for (String symb : vocab) {
			activate(new String[] { symb }, mods);

			multiword_symbol_treatment(0, mods, syc);

			int n_subsymbols = syc.n_symbols(symb);
			int i;
			for (i = 1; i < Math.min(mods.length, n_subsymbols); i++) {
				assertTrue(mods[i].is_frozen());
				assertEquals(0, mods[i].get_expectation().length);
			}

			for (; i < mods.length; i++) {
				assertTrue("@mod " + i + ", symb: " + symb,
						!mods[i].is_frozen());
			}

			clean(mods);
		}

	}
}
