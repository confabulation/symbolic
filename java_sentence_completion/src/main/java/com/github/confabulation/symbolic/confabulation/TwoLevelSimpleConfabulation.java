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

import java.io.IOException;
import java.util.Arrays;

import com.github.confabulation.symbolic.parser.GenericSymbolParser;
import com.github.confabulation.symbolic.parser.SymbolOrganizer;
import com.github.confabulation.symbolic.utils.ArrayTools;
import com.github.confabulation.symbolic.utils.ArrayTools2D;
import com.github.confabulation.symbolic.utils.MathTools;

/**
 * The two level architecture for word completion, with simple confabulation
 * 
 * @author bernard and cedric
 * 
 */
public class TwoLevelSimpleConfabulation extends ForwardConfabulation {

	/**
	 * word modules, also represented in the first half of
	 * {@link ConfabulationStub#mods}
	 */
	protected Module[] w;
	/**
	 * phrase modules, second half of {@link ConfabulationStub#mods}
	 */
	protected Module[] p;

	/**
	 * Maximum number of sub symbols allowed in a multi symbol
	 */
	private int MAX_SUBSYMBOLS = 3;

	/**
	 * word modules to phrase modules knowledge bases, extracted from the global
	 * {@link ConfabulationStub#kbs}
	 */
	protected KnowledgeBase[][] w_to_p;

	/**
	 * phrase modules to word modules knowledge bases, extracted from the global
	 * {@link ConfabulationStub#kbs}
	 */
	protected KnowledgeBase[][] p_to_w;

	/**
	 * phrase modules to phrase modules knowledge bases, extracted from the
	 * global {@link ConfabulationStub#kbs}
	 */
	protected KnowledgeBase[][] p_to_p;
	/**
	 * word modules to word module knowledge bases, extracted from the global
	 * {@link ConfabulationStub#kbs}
	 */
	protected KnowledgeBase[][] w_to_w;

	public TwoLevelSimpleConfabulation(int n_word_modules, String filename)
			throws IOException {
		super(n_word_modules);
		int tot_n_mods = n_word_modules + n_word_modules;

		// kbs and kbs_spec are organized like:
		//
		// src: |
		// -----+--------+--------+
		// W -> | w_to_w | w_to_p |
		// -----+--------+--------+
		// P -> | p_to_w | p_to_p |
		// -----+--------+--------+
		// targ: - - W - - - P -

		boolean[][] kbs_spec = new boolean[tot_n_mods][tot_n_mods];

		// === intra-level knowledge bases ===
		// each module sends a kb to the following ones

		// W -> W
		for (int src = 0; src < n_word_modules; src++) {
			for (int targ = src + 1; targ < n_word_modules; targ++) {
				kbs_spec[src][targ] = true;
			}
		}

		// P -> P
		for (int src = n_word_modules; src < tot_n_mods; src++) {
			for (int targ = src + 1; targ < tot_n_mods; targ++) {
				kbs_spec[src][targ] = true;
			}
		}

		// === inter-level knowledge bases ===

		// W -> P
		// each word module sends a kb to the P modules above and before
		for (int src = 0; src < n_word_modules; src++) {
			for (int targ = n_word_modules; targ <= src + n_word_modules; targ++) {
				kbs_spec[src][targ] = true;
			}
		}

		// P -> W
		// each phrase module sends a kb to the W modules below and after
		for (int src = n_word_modules; src < tot_n_mods; src++) {
			for (int targ = src - n_word_modules; targ < n_word_modules; targ++) {
				kbs_spec[src][targ] = true;
			}
		}

		GenericSymbolParser parser = new GenericSymbolParser(filename);
		SymbolMapping[] maps = identify_vocab(parser, MAX_SUBSYMBOLS, null, 0);

		SymbolMapping[] maps_by_module = ArrayTools.new_fill(tot_n_mods, maps);
		build(maps_by_module, kbs_spec);

		SymbolConverter[] sycs = ArrayTools.new_fill(2, new SymbolConverter());
		int[] level_sizes = new int[] { n_word_modules, n_word_modules };
		SymbolOrganizer org = new MultiLevelOrganizer(level_sizes, maps, sycs);
		learn(parser, org);

		parser.close();

		w = Arrays.copyOf(mods, n_word_modules);
		p = Arrays.copyOfRange(mods, n_word_modules, tot_n_mods);

		// aliases for sub parts: see comment about kbs above
		w_to_w = ArrayTools2D.copyOfRectangle(kbs, 0, 0, n_word_modules,
				n_word_modules);
		w_to_p = ArrayTools2D.copyOfRectangle(kbs, 0, n_word_modules,
				n_word_modules, n_word_modules + p.length);
		p_to_w = ArrayTools2D.copyOfRectangle(kbs, n_word_modules, 0,
				tot_n_mods, n_word_modules);
		p_to_p = ArrayTools2D.copyOfRectangle(kbs, n_word_modules,
				n_word_modules, tot_n_mods, tot_n_mods);

	}

	@Override
	public String next_word(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {

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

		activate(symbols, w);

		// re-represent conservatively the word level
		Module[] previous_p = Arrays.copyOf(p, p.length);
		Arrays.fill(previous_p, index, p.length, null);
		conservative_rerepr(w, previous_p, w_to_p, syc); 
		
		// This is not enough: still to unexpected results (see test next_word2())
		// TODO complete uncertain modules before completing, in another class

		// CT:TMT p.157
		// expectation with C1F on P to complete
		collect_all_input(previous_p, p_to_p, index, p[index]);
		p[index].partial_confabulation(1, false);

		// transmission to W module to complete and C1F
		transfer_excitation(p[index], p_to_w[index][index], w[index]);
		w[index].partial_confabulation(1, false);

		// knowledge links from the rest of the pharse module
		collect_all_input(previous_p, p_to_w, index, w[index]);

		// WF on module to complete
		String ret = w[index].elementary_confabulation();
		clean();
		return ret;
	}

	/**
	 * Re-represent the words on from a lower level to the upper level
	 * conservatively
	 * <p>
	 * Transfer the excitation from the source module with the closest index to
	 * the current target model, then perform a CKF, with K = 1. Increment K,
	 * and repeat with the next closest module. A positive shift has priority
	 * over a negative shift of the same magnitude.
	 * </p>
	 * <p>
	 * Implementation notes
	 * </p>
	 * <p>
	 * We tried to decrease the excitation levels instead of increasing K, but
	 * it did not worked out well: the resulting excitations could become zero
	 * and be excluded from the expectation. Therefore, increasing K is used.
	 * </p>
	 * <p>
	 * An alternative to this is to keep only the excitations that received
	 * input from the most knowledge bases, with C(-1)F. It could give more
	 * tolerance when the active word does not send any link to any symbols of
	 * the already present expectation.
	 * </p>
	 * <p>
	 * In this case, the K method blanks the current module, instead of letting
	 * the expectation as-is.
	 * </p>
	 * <p>
	 * Arguments must be non-null, and their lengths must be non-zero
	 * </p>
	 * 
	 * @param srcs
	 *            padding with null values is allowed, length can be different
	 *            than targ_level.length
	 * @param targs
	 *            padding with null values is allowed, length can be different
	 *            than targ_level.length
	 * @param kbs
	 *            kbs.length == src_level.length; kbs[i].length ==
	 *            targ_level.length. Any element kbs[i][j] can be null.
	 */
	public static void conservative_rerepr(Module[] srcs, Module[] targs,
			KnowledgeBase[][] kbs, SymbolConverter syc) {

		if (srcs.length != kbs.length) {
			throw new IllegalArgumentException("lengths: src(" + srcs.length
					+ ") != kbs(" + kbs.length + ")");
		}
		if (targs.length != kbs[0].length) {
			throw new IllegalArgumentException("lengths: targs(" + targs.length
					+ ") != kbs[0](" + kbs[0].length + ")");
		}

		for (int t = 0; t < targs.length; t++) {
			// if non-null and not locked
			if (targs[t] != null
					&& !(targs[t].is_frozen() && targs[t].get_expectation().length == 0)) {

				int K = 1; // CKF counter: increase on each input
				String[] exp = null;
				int shift = 0;
				while (shift != -srcs.length && (exp == null || exp.length > 1)) {

					int src = t + shift;
					if (0 <= src && src < srcs.length && kbs[src][t] != null
							&& srcs[src] != null
							&& srcs[src].get_expectation().length > 0) {

						transfer_excitation(srcs[src], kbs[src][t], targs[t]);
						exp = targs[t].partial_confabulation(K, false);
						K++;
					}

					// 0, 1, -1, 2, -2, 3, -3, ...
					if (shift > 0) {
						shift = -shift;
					} else {
						shift = -shift + 1;
					}
				}

				multiword_symbol_treatment(t, targs, syc);
			}
		}
	}

	/**
	 * Lock the modules spanned by the multi symbols, if all the possible conclusion symbols are multi symbols. 
	 * 
	 * @param mod_index in [0, mods.length[
	 * @param mods non-null, mods[mod_index] non null
	 * @param syc non-null
	 */
	protected static void multiword_symbol_treatment(int mod_index,
			Module[] mods, SymbolConverter syc) {

		String[] expectation = mods[mod_index].get_expectation();
		if (expectation.length == 0) {
			return;
		}
		
		int[] n_words_of_symbs = new int[expectation.length];
		for (int i = 0; i < expectation.length; i++) {
			n_words_of_symbs[i] = syc.n_symbols(expectation[i]);
		}
		
		if (MathTools.min(n_words_of_symbs) != 1) { // only multiword symbols

			int n_spanned_modules = MathTools.min(n_words_of_symbs);
			for (int i = mod_index + 1; i < Math.min(mods.length, mod_index
					+ n_spanned_modules); i++) {

				if (mods[i] != null) {
					// lock module
					mods[i].excitations_to_zero();
					mods[i].freeze();
				}
			}
		}
	}

	// Does this also work downwards??? ... yes but will yield incoherent
	// results

	@Override
	public String[] next_expectation(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {
		return new String[] { next_word(symbols, index_to_complete) };
	}
}