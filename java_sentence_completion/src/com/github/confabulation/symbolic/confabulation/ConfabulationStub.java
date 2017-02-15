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

package com.github.confabulation.symbolic.confabulation;

import static java.util.Arrays.copyOfRange;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.github.confabulation.symbolic.parser.GenericSymbolParser;
import com.github.confabulation.symbolic.parser.SymbolOrganizer;
import com.github.confabulation.symbolic.parser.WordSymbols;
import com.github.confabulation.symbolic.utils.ArrayTools;
import com.github.confabulation.symbolic.utils.ComparablePair;
import com.github.confabulation.symbolic.utils.RuntimeIOException;

/**
 * Base class for Confabulation.
 * <p>
 * Contains naive building blocs for the forward confabulation and the basic
 * multiconfabulation
 * </p>
 * <p>
 * Ideally, a subclass only has only to specify which knowledge bases have to be
 * created, and what are the arguments that allow a completion. For instance, a
 * confabulation experiment with 4 sources modules and a single target module
 * can specify that there is only 4 knowledge bases, and that the only possible
 * index to perform a confabulation is the index of the target module.
 * </p>
 * 
 * @author bernard and cedric
 */
public abstract class ConfabulationStub implements SentenceCompletionIF {

	/**
	 * Number of knowledge link inputs. Auto-detected when K <= 0
	 */
	protected int K = -1;

	/**
	 * all the modules of the architecture
	 */
	protected Module[] mods;

	/**
	 * The knowledge bases, in (src, targ) coordinates
	 */
	protected KnowledgeBase[][] kbs;

	protected SymbolConverter syc = new SymbolConverter();

	/**
	 * Creates and returns a list of {@link SymbolMapping} according to the
	 * given specifications
	 * 
	 * @param vocab_specs
	 *            a list of specifications of the number of multiword modules to
	 *            keep.
	 *            <ul>
	 *            <li>if vocab_specs[i] is null, no multi module symbol...</li>
	 *            <li>if vocab_specs[i] == 0, all multi module symbols...</li>
	 *            <li>if (x = vocab_specs[i]) > 0, only the x most frequent
	 *            multi-module symbols...</li>
	 *            <li>if (x = vocab_specs[i]) > 0, all the multi module symbols
	 *            will be kept, except the |x| least frequent symbols,...</li>
	 *            </ul>
	 *            ... are kept in returned symbolmapping[i]
	 * @return a list of new symbolmappings, one for each spec, in the same
	 *         order of the specs
	 */
	protected SymbolMapping[] identify_vocab(GenericSymbolParser p,
			int max_words, Integer... vocab_specs) {
		p.restart_if_used();

		// ========= Method =========
		// - multi word occurrence count (single word count also done here)
		// - building of the mappings with symbol selection

		// ========= parameters =========

		final int MIN_OCC = 2; // minimum number of occurrences for a multi-word
		final int MAX_WORDS = max_words; // maximum number of words to consider
											// in a multi-word

		// ========= multi word occurrence count =========
		// target: get the number of occurrences of multi-word symbols, favoring
		// the longest multi-word symbols
		//
		// algo:
		// - count occurrence only if prefix occurred at least MIN_OCC times
		// - then remove counts of the longer occurrences from sub-symbols since
		// they are part of the longer one
		// - finally remove the multiwords with not enough occurrences
		//
		// algo rationale: spare memory because n^MAX_WORD can easily be big

		Vector<HashMap<List<String>, Integer>> occurrence_counts = new Vector<HashMap<List<String>, Integer>>();
		occurrence_counts.setSize(MAX_WORDS + 1);

		// count occurrences of single words, to serve as basis
		HashMap<List<String>, Integer> single_occ_count = new HashMap<List<String>, Integer>();
		occurrence_counts.set(1, single_occ_count);

		String[] line;
		while ((line = p.getLine()) != null) {
			for (String w : line) {

				List<String> occ = Arrays.asList(new String[] { w });
				Integer count = single_occ_count.get(occ);
				if (count == null) {
					single_occ_count.put(occ, 1);
				} else {
					single_occ_count.put(occ, count + 1);
				}
			}
		}
		p.restart();

		// for each possible multiword length,
		// count occurrences whose prefixes occured at least MIN_OCC times

		for (int n_words = 2; n_words <= MAX_WORDS; n_words++) {
			HashMap<List<String>, Integer> prev_occ_count = occurrence_counts
					.get(n_words - 1);
			HashMap<List<String>, Integer> occ_count = new HashMap<List<String>, Integer>();
			occurrence_counts.set(n_words, occ_count);

			while ((line = p.getLine()) != null) {
				for (int i = 0; i <= line.length - n_words; i++) {
					if (!ArrayTools.in(null, line, i, i + n_words)) {

						List<String> prefix = Arrays.asList(//
								copyOfRange(line, i, i + n_words - 1));
						Integer prefix_count = prev_occ_count.get(prefix);
						if (prefix_count != null && prefix_count >= MIN_OCC) {

							// count occurrence
							List<String> occ = Arrays.asList(//
									copyOfRange(line, i, i + n_words));
							Integer count = occ_count.get(occ);

							if (count != null) {
								occ_count.put(occ, count + 1);
							} else {
								occ_count.put(occ, 1);
							}
						}
					}
				}
			}
			p.restart();
		}

		// remove counts of longer groups from the count of the sub groups
		// -> Favour "all of a sudden" instead of "all of a"
		for (int n_words = 2; n_words <= MAX_WORDS; n_words++) {
			HashMap<List<String>, Integer> prev_occ = occurrence_counts
					.get(n_words - 1);

			for (Entry<List<String>, Integer> e : occurrence_counts
					.get(n_words).entrySet()) {

				// TODO fix problem: with this algo, "all of" can still subsist, even if
				// it only occurred as part of "all of a sudden"
				// => must go through all the subparts !!!
				// TODO add improved tests to check this
				int count = e.getValue();
				if (count >= MIN_OCC) {
					List<String> prefix = e.getKey().subList(0, n_words - 1);
					prev_occ.put(prefix, prev_occ.get(prefix) - count);

					List<String> suffix = e.getKey().subList(0, n_words - 1);
					prev_occ.put(suffix, prev_occ.get(suffix) - count);
				}
			}
		}

		// clean the groups with less than MIN_OCC occurrences
		// start at two: keep all the single word symbols
		for (int n_words = 2; n_words <= MAX_WORDS; n_words++) {
			HashMap<List<String>, Integer> occ_count = occurrence_counts
					.get(n_words);
			Set<Entry<List<String>, Integer>> counts = new HashSet<Entry<List<String>, Integer>>(
					occ_count.entrySet());

			for (Entry<List<String>, Integer> e : counts) {
				if (e.getValue() < MIN_OCC) {
					occ_count.remove(e.getKey());
				}
			}
		}

		// ========= symbolmapping creation & build =========

		// sort all the occurrence-symbols
		TreeSet<ComparablePair<Integer, String>> by_count = new TreeSet<ComparablePair<Integer, String>>();

		for (int n_words = 2; n_words <= MAX_WORDS; n_words++) {
			for (Entry<List<String>, Integer> e : occurrence_counts
					.get(n_words).entrySet()) {
				String symbol = syc.string_symbol(e.getKey());

				by_count.add(new ComparablePair<Integer, String>(e.getValue(),
						symbol));
			}
		}

		// fill mappings
		SymbolMapping[] mappings = new SymbolMapping[vocab_specs.length];
		for (int i = 0; i < vocab_specs.length; i++) {
			mappings[i] = new SymbolMapping();

			// TODO implement clone, or such, in SymbolMapping
			// to do this (just below) only once
			for (List<String> s_array : single_occ_count.keySet()) {
				mappings[i].add_symbol(s_array.get(0));
			}

			if (vocab_specs[i] != null) {
				int limit;
				if (vocab_specs[i] <= 0) {
					limit = vocab_specs[i];
					for (int n_words = 2; n_words <= MAX_WORDS; n_words++) {
						limit += occurrence_counts.get(n_words).size();
					}
				} else {
					limit = vocab_specs[i];
				}

				Iterator<ComparablePair<Integer, String>> top_counts = by_count
						.descendingIterator();
				while (limit > 0 && top_counts.hasNext()) {

					// cur == (number_of_modules_symbol, symbol)
					ComparablePair<Integer, String> cur = top_counts.next();
					mappings[i].add_symbol(cur.second);
				}
			}
		}

		return mappings;
	}

	/**
	 * build the modules and the knowledge bases
	 * <p>
	 * As a side-effect, this sets the fields {@link #mods} and {@link #kbs}
	 * </p>
	 * 
	 * @param mappings
	 *            the {@link SymbolMapping} to for each module creation.
	 *            <p>
	 *            Must be: mappings.length == kb_spec.length == n_modules
	 *            </p>
	 * @param kbs_spec
	 *            the specification of which knowledge bases to create, in
	 *            (source, target) format. Example:
	 *            <p>
	 *            kbs_spec[0][3] = true creates a knowledge base from module 0
	 *            to module 3
	 *            </p>
	 * @see #mods
	 * @see #kbs
	 */
	protected void build(SymbolMapping[] mappings, boolean[][] kbs_spec) {

		int n_mods = kbs_spec.length;

		// create the modules
		mods = new Module[n_mods];
		for (int i = 0; i < n_mods; i++) {
			mods[i] = new Module(mappings[i]);
		}

		// create the knowledge bases according to matrix
		kbs = new KnowledgeBase[n_mods][n_mods];
		for (int i = 0; i < n_mods; i++) {
			for (int j = 0; j < n_mods; j++) {

				if (kbs_spec[i][j]) {
					kbs[i][j] = new KnowledgeBase(mods[i].sm, mods[j].sm);
				}
			}
		}
	}

	/**
	 * build all the knowledge links in each knowledge base according to the
	 * data read from the parser
	 * 
	 * @param parser
	 *            non-null, reads a correctly formated corpus file
	 * @param org
	 *            non-null,
	 * @throws RuntimeIOException
	 * @throws ConfabulationException
	 */
	protected void learn(GenericSymbolParser parser, SymbolOrganizer org)
			throws RuntimeIOException, ConfabulationException {
		int n_mods = kbs.length;

		// wire the knowledge links
		String[] word_symbols;
		parser.restart_if_used();
		while ((word_symbols = parser.getLine()) != null) {
			String[][] symbols4module = org.organize(word_symbols);

			String[][] clean_symb4mod = ArrayTools.replace_all_by(
					new String[][] { null, new String[0] },
					new String[] { null }, symbols4module);
			String[][] symbols_by_module = ArrayTools
					.enumerate_all_possibilities(clean_symb4mod);

			for (String[] combinaison_symb : symbols_by_module) {

				for (int src = 0; src < n_mods; src++) {
					if (combinaison_symb[src] != null) {
						for (int targ = 0; targ < n_mods; targ++) {
							if (kbs[src][targ] != null
									&& combinaison_symb[targ] != null) {

								try {
									kbs[src][targ].add(combinaison_symb[src],
											combinaison_symb[targ]);
								} catch (IndexOutOfBoundsException e) {
									System.exit(-1);
								}
							}
						}
					}
				}
			}
		}

		// compute knowledge link strengths
		for (KnowledgeBase[] kb_from : kbs) {
			for (KnowledgeBase k : kb_from) {
				if (k != null) {
					k.compute_link_strengths();
				}
			}
		}
	}

	/**
	 * Number of knowledge link inputs. Auto-detected when K < 0.
	 * <p>
	 * Default: autodetect
	 * </p>
	 */
	public int getK() {
		return K;
	}

	/**
	 * Number of knowledge link inputs. Auto-detected when K < 0
	 */
	public void setK(int k) {
		K = k;
	}

	/**
	 * autodetect the index to complete
	 * 
	 * @param symbols
	 *            the list symbols to complete
	 * @return an index to complete that can lead to a completion, or -1 if no
	 *         valid position was found
	 */
	protected abstract int auto_index_to_complete(String[] symbols);

	/**
	 * activate each symbol in the corresponding module, and freeze the module
	 * 
	 * @param symbols
	 *            non-null. If an element is null, skip the corresponding module
	 * @param mods
	 *            non-null, the modules where a symbol can be activated. if an
	 *            element is null, skip the corresponding symbol
	 */
	protected static void activate(String[] symbols, Module[] mods)
			throws ConfabulationException {

		for (int i = 0; i < Math.min(symbols.length, mods.length); i++) {
			if (symbols[i] != null && mods[i] != null) {
				mods[i].activate_word(symbols[i], 1);
				mods[i].freeze();
			}
		}
	}

	/**
	 * erase the content of the modules
	 */
	protected void clean() {
		clean(mods);
	}

	/**
	 * erase the content of the modules
	 * 
	 * @param mods
	 *            non-null list of modules, can contain null values
	 */
	protected static void clean(Module[] mods) {
		for (Module module : mods) {
			if (module != null) {
				module.reset();
				module.unfreeze();
			}
		}
	}

	/**
	 * compute the actual K to be used
	 * 
	 * @param K
	 *            Number of knowledge link inputs. Auto-detected when K < 0.
	 * @param symbols
	 *            ok with {@link #check_argument(String[], int)}
	 * @param index_to_complete
	 *            positive
	 * @return max_K if K < 0 or min(K, max_K) if K >= 0; where max_K = number
	 *         of non-null symbols before index_to_complete
	 */
	public int actual_K(int K, String[] symbols, int index_to_complete) {
		if (index_to_complete < 0) {
			throw new IllegalArgumentException("index_to_complete is < 0");
		}
		int index = Math.min(symbols.length, index_to_complete);

		// number of non-null symbols before index_to_complete
		int max_K = index
				- ArrayTools.number_equal(null, Arrays.copyOf(symbols, index));

		if (K >= 0) {
			return Math.min(K, max_K);
		} else {
			// autocompute: use max_K
			return max_K;
		}
	}

	@Override
	public String[] next_expectation(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {
		return confabulation(symbols, index_to_complete, true);
	}

	@Override
	public String next_word(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {
		return confabulation(symbols, index_to_complete, false)[0];
	}

	/**
	 * Find the next word, or the next set of expectations
	 * 
	 * @param symbols
	 *            the symbol representation of the sentence. Special values:
	 *            <ul>
	 *            <li>null value indicates the absence of symbol at the given
	 *            position.
	 *            <li>empty string "" notes special empty symbol due to a
	 *            multi-word symbol
	 *            </ul>
	 *            <p>
	 *            non null
	 *            </p>
	 * @param index_to_complete
	 *            when it's positive, it is the index to complete. If
	 *            <em>index_to_complete</em> is negative, it is autodetected
	 * @param expectation
	 *            <ul>
	 *            <li>false => find the next word</li>
	 *            <li>true => find the next set of expectations</li>
	 *            </ul>
	 * @return a list containing a single element (see
	 *         {@link #next_word(String[], int)}), if <em>expectation</em> is
	 *         false, or the list of expectations (see
	 *         {@link #next_expectation(String[], int)})
	 * @throws IllegalArgumentException
	 *             when the pair (symbols, index_to_complete) is invalid
	 * @see #check_argument(String[], int)
	 * @see #next_word(String[], int)
	 * @see #next_expectation(String[], int)
	 */
	protected String[] confabulation(String[] symbols, int index_to_complete,
			boolean expectation) throws IllegalArgumentException {

		String check_result = check_argument(symbols, index_to_complete);
		if (!"ok".equals(check_result)) {
			throw new IllegalArgumentException(check_result);
		}

		int index;
		if (index_to_complete < 0) {
			index = auto_index_to_complete(symbols);
		} else {
			index = index_to_complete;
		}

		int actual_K = actual_K(K, symbols, index);
		Module targ = mods[index];

		// core algo
		activate(symbols, mods);
		// collect all input
		collect_all_input(mods, kbs, index, mods[index]);
		String[] ret;

		if (expectation) {
			ret = targ.partial_confabulation(actual_K, false);
		} else {
			ret = new String[] { targ.elementary_confabulation(actual_K) };
		}

		clean();
		return ret;
	}

	/**
	 * Transfer the excitation from each of the srcs modules to the targ module
	 * whose knowledge bases are in column targ_index in kbs
	 * 
	 * @param srcs
	 *            non-null, the source modules. Null elements are skipped. Must
	 *            be as long as kbs
	 * @param kbs
	 *            the knowledge bases. Null elements are skipped kbs.length must
	 *            be equal to mods.length.
	 * @param targ_index
	 *            must be in [0, kbs[i].length[ for each i
	 * @param targ
	 *            non-null, the target module
	 * 
	 * @throws IllegalArgumentException
	 *             if the lengths of srcs and kbs differ, or if the number of
	 *             symbols of a module differ with the size of the knowledge
	 *             base
	 * @throws NullPointerException
	 *             if targ is null
	 * @throws IndexOutOfBoundsException
	 *             if targ_index is out of bounds
	 */
	protected static void collect_all_input(Module[] srcs,
			KnowledgeBase[][] kbs, int targ_index, Module targ)
			throws IllegalArgumentException, NullPointerException,
			IndexOutOfBoundsException {
		if (srcs.length != kbs.length) {
			throw new IllegalArgumentException("srcs.length (" + srcs.length
					+ ") != kbs.length(" + kbs.length + ")");
		}

		for (int i = 0; i < kbs.length; i++) {
			if (kbs[i][targ_index] != null && srcs[i] != null) {
				transfer_excitation(srcs[i], kbs[i][targ_index], targ);
			}
		}
	}

	/**
	 * transfers the excitation of src over kb and adds the resulting input to
	 * targ
	 * 
	 * @param src
	 *            non-null source module
	 * @param kb
	 *            non-null src to targ knowledge base
	 * @param targ
	 *            non-null target module, whose excitations will be modified by
	 *            this call
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 * @see KnowledgeBase#transmit(com.github.confabulation.symbolic.sparse.float_.DOK1Dfloat)
	 */
	protected static void transfer_excitation(Module src, KnowledgeBase kb,
			Module targ) throws IllegalArgumentException, NullPointerException {
		// DEBUG
		if (ArrayTools.in(null, src, kb, targ)) {
			String[] nulls = ArrayTools.get_map(null, //
					new Object[] { src, kb, targ }, //
					new String[] { "src", "kb", "targ" });
			throw new NullPointerException("null: "
					+ ArrayTools.join(", ", nulls));
		}
		targ.add_excitations(kb.transmit(src.getNormalised_excitations()));
	}

	/**
	 * check whether each symbol is in the vocabulary of each module
	 * 
	 * @param symbols
	 * @return
	 */
	protected String check_vocabulary(String[] symbols) {
		for (int i = 0; i < Math.min(symbols.length, mods.length); i++) {
			if (symbols[i] != null && !mods[i].sm.contains(symbols[i])) {
				return "symbol "
						+ i
						+ " ("
						+ symbols[i]
						+ ") "
						+ "is not in the vocabulary of the corresponding module";
			}
		}
		return "ok";
	}

	/**
	 * Initialization for single level word-only architectures
	 * 
	 * @param kbs_spec
	 *            the description of the knowledge links to create, lines are
	 *            source indexes, columns are target indexes. See
	 *            {@link #build(SymbolMapping[], boolean[][])}
	 * @param filename
	 *            the path of the corpus
	 * @throws IOException
	 *             if the corpus can't be read
	 */
	protected void init(boolean[][] kbs_spec, String filename)
			throws IOException {

		int n_modules = kbs_spec.length;
		GenericSymbolParser p = new GenericSymbolParser(filename);

		SymbolMapping sm = identify_vocab(p, 1, (Integer) null)[0];
		SymbolMapping[] mappings = ArrayTools.new_fill(n_modules, sm);

		build(mappings, kbs_spec);

		learn(p, new WordSymbols(n_modules));

		p.close();
	}

	/**
	 * Get a string representation of all the expectations of the modules
	 * 
	 * @param mods
	 *            non-null
	 * @return
	 */
	public static String all_expectations(Module[] mods) {
		String[] ret = new String[mods.length];
		for (int i = 0; i < mods.length; i++) {
			if (mods[i] != null) {
				ret[i] = i + ": " + Arrays.toString(mods[i].get_expectation());
			}
		}
		return ArrayTools.join("\n", ret);
	}
}
