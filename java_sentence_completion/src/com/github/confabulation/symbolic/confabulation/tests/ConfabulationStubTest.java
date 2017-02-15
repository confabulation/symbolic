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

package com.github.confabulation.symbolic.confabulation.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.confabulation.symbolic.parser.GenericSymbolParser;
import com.github.confabulation.symbolic.parser.SymbolOrganizer;
import com.github.confabulation.symbolic.utils.ArrayTools;
import com.github.confabulation.symbolic.utils.RuntimeIOException;
import com.github.confabulation.symbolic.confabulation.ConfabulationStub;
import com.github.confabulation.symbolic.confabulation.KnowledgeBase;
import com.github.confabulation.symbolic.confabulation.Module;
import com.github.confabulation.symbolic.confabulation.MultiLevelOrganizer;
import com.github.confabulation.symbolic.confabulation.SymbolConverter;
import com.github.confabulation.symbolic.confabulation.SymbolMapping;

public class ConfabulationStubTest extends ConfabulationStub {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private File f;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		// cleanup
		mods = null;
		kbs = null;

		if (f != null) {
			f.delete();
		}
	}

	@Test
	public void testIdentify_vocab() throws IOException {
		String[] corpus = {
				// CORPUS
				"I am bernard", //
				"I feel tired", //
				"I am hungry" //
		};
		f = temp_file("ConfabulationStubTestIdentifyVocab", corpus);
		GenericSymbolParser p = new GenericSymbolParser(f.getAbsolutePath());
		Integer[] sm_specs = { null, 0 };

		SymbolMapping[] sms = identify_vocab(p, 2, sm_specs);

		assertEquals(sm_specs.length, sms.length);

		LinkedList<Set<String>> vocabs = new LinkedList<Set<String>>();
		for (SymbolMapping sm : sms) {
			vocabs.add(sm.get_all_symbols());
		}

		String[] expected_vocab_null = { "I", "am", "bernard", "feel", "tired",
				"hungry" };
		String[] expected_vocab_0 = Arrays.copyOf(expected_vocab_null,
				expected_vocab_null.length + 1);
		expected_vocab_0[expected_vocab_null.length] = syc
				.string_symbol(new String[] { "I", "am" });
		String[][] expected_vocabs = { expected_vocab_null, expected_vocab_0 };

		for (int i = 0; i < sms.length; i++) {
			Set<String> vocab = vocabs.get(i);
			assertEquals("i:" + i + " actual vocab: " + vocab + "\n expected:"
					+ Arrays.toString(expected_vocabs[i]),
					expected_vocabs[i].length, vocab.size());

			for (String symb : expected_vocabs[i]) {
				assertTrue("symb: " + symb + " missing", vocab.contains(symb));
			}
		}
	}

	@Test
	public void testBuild() {

		String[] vocab = { "feel", "the", "rhythm" };
		SymbolMapping sm = new_sm(vocab);

		String[] vocab2 = ArrayTools.concat(vocab, new String[] { "random" });
		SymbolMapping sm2 = new_sm(vocab2);

		SymbolMapping[] mappings = ArrayTools.new_fill(3, sm, sm2);

		boolean[][] kbs_spec = { { true, false, true }, { false, true, false },
				{ true, true, true } };
		build(mappings, kbs_spec);

		assertEquals(mappings.length, mods.length);
		for (Module m : mods) {
			assertNotNull(m);
		}

		assertEquals(kbs_spec.length, kbs.length);
		for (int i = 0; i < kbs_spec.length; i++) {
			assertEquals(kbs_spec[i].length, kbs[i].length);

			for (int j = 0; j < kbs_spec[i].length; j++) {
				if (kbs_spec[i][j]) {
					assertNotNull(kbs[i][j]);
					assertEquals(mappings[i].size(), kbs[i][j].size_src());
					assertEquals(mappings[j].size(), kbs[i][j].size_targ());
				} else {
					assertNull(kbs[i][j]);
				}
			}
		}
	}

	public static SymbolMapping new_sm(String[] vocab) {
		SymbolMapping sm = new SymbolMapping();
		for (String symb : vocab) {
			sm.add_symbol(symb);
		}
		return sm;
	}

	@Test
	public void testLearn() throws IOException {

		// === setup ===
		String[] corpus = {
				// CORPUS
				"I am bernard", //
				"I feel tired", //
				"I am hungry" //
		};
		f = temp_file("ConfabulationStubTestIdentifyVocab", corpus);
		GenericSymbolParser p = new GenericSymbolParser(f.getAbsolutePath());
		// TODO enhance test to check more "max_words" values
		// initial test used "3", but this led to undetected bugs
		SymbolMapping[] sms = identify_vocab(p, 2, (Integer) null, 0);

		final int n_w = 3;
		SymbolMapping[] mappings = ArrayTools.new_fill(n_w * 2, sms);
		boolean[][] kbs_spec = {
				// (src, targ) organisation
				// low-level - - - - - - - high-level
				{ false, true, true, /**/true, false, false }, // <- low
				{ false, false, true, /**/true, true, false },// <- low
				{ false, false, false, /**/true, true, true }, // <- low
				//
				{ true, true, true, /**/false, true, true }, // <- high
				{ false, true, true, /**/false, false, true }, // <- high
				{ false, false, true, /**/false, false, false }, // <- high
		};

		build(mappings, kbs_spec);

		SymbolOrganizer org = new MultiLevelOrganizer(new int[] { n_w, n_w },
				sms, new SymbolConverter[] { syc, syc });
		learn(p, org);

		// === test ===

		assertEquals(2, kbs[0][n_w + 0].n_knowledge_links());
		assertEquals(1, kbs[1][n_w + 1].n_knowledge_links());
		assertEquals(3, kbs[2][n_w + 2].n_knowledge_links());

		assertEquals(2, kbs[n_w + 0][0].n_knowledge_links());
		assertEquals(1, kbs[n_w + 1][1].n_knowledge_links());
		assertEquals(3, kbs[n_w + 2][2].n_knowledge_links());

		assertEquals(2, kbs[1][n_w + 0].n_knowledge_links());
		assertEquals(1, kbs[2][n_w + 1].n_knowledge_links());

		assertEquals(2, kbs[n_w + 0][1].n_knowledge_links());
		assertEquals(1, kbs[n_w + 1][2].n_knowledge_links());

		assertEquals(3, kbs[n_w + 0][2].n_knowledge_links());

		assertEquals(3, kbs[2][n_w + 0].n_knowledge_links());
	}

	public static void assertArrayEqualsDBG(Object[] expected, Object[] actual) {
		assertArrayEqualsDBG("", expected, actual);
	}

	public static void assertArrayEqualsDBG(String message, Object[] expected,
			Object[] actual) {
		String msg = message + "\n" + "expected: "
				+ Arrays.deepToString(expected) + "\n" + "actual: "
				+ Arrays.deepToString(actual);
		assertArrayEquals(msg, expected, actual);
	}

	@Test
	public void testActivate() {
		SymbolMapping sm = new SymbolMapping();
		String[] vocab = { "nothing", "lasts", "but", "nothing", "is", "lost" };
		for (String symb : vocab) {
			sm.add_symbol(symb);
		}
		Module[] mods = { new Module(sm), new Module(sm), new Module(sm) };

		String[] symbols = { "nothing", "is", "lost" };
		activate(symbols, mods);

		String[][] expecteds = ArrayTools.zip(symbols);
		for (int i = 0; i < expecteds.length; i++) {
			assertArrayEquals(expecteds[i], mods[i].get_expectation());
		}
	}

	@Test
	public void testClean() {
		SymbolMapping sm = new SymbolMapping();
		final String symbol = "42";
		String[] vocab = { symbol };
		for (String symb : vocab) {
			sm.add_symbol(symb);
		}
		if (mods == null) {
			mods = new Module[10];
		}

		for (int i = 0; i < mods.length; i++) {
			mods[i] = new Module(sm);
			mods[i].activate_word(symbol, 1);
			mods[i].freeze();
		}

		clean();
		String[] empty = {};
		for (Module mod : mods) {
			assertArrayEquals(empty, mod.get_expectation());
			assertTrue(!mod.is_frozen());
		}
	}

	@Test
	public void testCollect_all_input() {
		SymbolMapping sm = new SymbolMapping();
		String[] vocab = { "A", "B" };
		for (String symb : vocab) {
			sm.add_symbol(symb);
		}

		Module[] srcs = { new Module(sm), new Module(sm), new Module(sm) };
		KnowledgeBase[][] kbs = //
		{ { null, null, new KnowledgeBase(sm, sm) }, //
				{ null, null, new KnowledgeBase(sm, sm) }, //
				{ null, null, null } };

		// learning
		kbs[0][2].add("A", "A");
		kbs[0][2].compute_link_strengths();
		kbs[1][2].add("B", "B");
		kbs[1][2].compute_link_strengths();

		String[][] symbolss = { {}, { "A" }, { null, "B" }, { "A", "B" } };
		String[][] expectations = { {}, { "A" }, { "B" }, { "A", "B" } };
		for (int i = 0; i < symbolss.length; i++) {
			String[] symbols = symbolss[i];
			String[] expectation = expectations[i];

			activate(symbols, srcs);
			collect_all_input(srcs, kbs, 2, srcs[2]);
			assertArrayEquals(expectation, srcs[2].get_expectation());

			clean(srcs);
		}
	}

	@Override
	public String check_argument(String[] symbols, int index_to_complete) {
		throw new UnsupportedOperationException("stub implementation");
	}

	@Override
	protected int auto_index_to_complete(String[] symbols) {
		throw new UnsupportedOperationException("stub implementation");
	}

	public static File temp_file(String name_prefix, String[] contents)
			throws RuntimeIOException {
		try {
			File f = File.createTempFile(name_prefix, "txt");

			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			try {
				for (String s : contents) {
					w.write(s);
					w.newLine();
				}
			} finally {
				if (w != null) {
					w.close();
				}
			}
			return f;
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

	}
}
