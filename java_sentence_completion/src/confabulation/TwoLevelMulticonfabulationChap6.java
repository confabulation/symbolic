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

package confabulation;

import static utils.ArrayTools.in;

import java.io.IOException;
import java.util.Arrays;

import utils.ArrayTools;

/**
 * Two-level multiconfabulation as described in the book, chapter 6, appendix A.
 * 
 * @author bernard and cedric
 * 
 */
public class TwoLevelMulticonfabulationChap6 extends
		TwoLevelSimpleConfabulation {

	/**
	 * the number of word modules to use in the multiconfabulation
	 */
	protected final int N_WORD_MODS_MULTICONF;

	public TwoLevelMulticonfabulationChap6(int n_word_modules, String filename)
			throws IOException {
		this(n_word_modules, filename, 4);
	}

	public TwoLevelMulticonfabulationChap6(int n_word_modules, String filename,
			int n_words_mods_multiconf) throws IOException {
		super(n_word_modules, filename);
		N_WORD_MODS_MULTICONF = n_words_mods_multiconf;
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

		return next_word_fragile(symbols, index);
	}

	/**
	 * Find the word filling the first hole, or the next word
	 * <p>
	 * <strong>Does not check preconditions, and can fail in unexpected ways.
	 * Use {@link #next_word(String[], int)}</strong>
	 * </p>
	 * 
	 * @param symbols
	 *            the symbol representation of the sentence.
	 * @param index
	 *            positive (>= 0), the position to fill in the symbols. No
	 * 
	 * 
	 * @return the symbol found, or null if no word was found
	 * @see #next_word(String[], int)
	 */
	protected String next_word_fragile(String[] symbols, int index) {

		activate(symbols, w);

		// re-represent conservatively the word level
		Module[] previous_p = Arrays.copyOf(p, p.length);
		Arrays.fill(previous_p, index, p.length, null);
		conservative_rerepr(w, previous_p, w_to_p, syc);

		// CT:TMT 6.A, p126-129
		// expectation with C1F on P to complete (light contraction)
		collect_all_input(previous_p, p_to_p, index, p[index]);
		String[] exp = p[index].partial_confabulation(1, false);

		// swirl over the different W modules while no conclusion
		int end = Math.min(w.length, index + N_WORD_MODS_MULTICONF);
		Module[] p_modules = ArrayTools.copy_indexes_fill_others(null,
				ArrayTools.range(index, end), p);

		int i = index;
		while (i < end && exp.length > 1) {

			basic_swirl(index, p_modules, p_to_w, w_to_p, i, w[i]);
			exp = p[index].get_expectation();
			i++;
		}

		if (exp.length > 1) {
			Module[] w_modules = ArrayTools.copy_indexes_fill_others(null,
					ArrayTools.range(index, end), w);

			exp = new String[] { after_swirls(w_modules, w_to_p, index,
					p_modules[index]) };
		}
		
		// re-represent solution in the W module
		transfer_excitation(p_modules[index], p_to_w[index][index], w[index]);
		String conclusion = w[index].elementary_confabulation();
		
		clean();

		return conclusion;
	}

	/**
	 * the basic swirl of chap 6.A ~not exactly... misses first C1F
	 * <p>
	 * The module p_module[p_index] is called P module here
	 * </p>
	 * <p>
	 * while the expectation on P module evolves; do
	 * <ul>
	 * <li>input from given p_modules to W module,</li>
	 * <li>
	 * C(-1)F on W module,</li>
	 * <li>transmit to P module,</li>
	 * <li>C(-1)F on P module</li>
	 * </ul>
	 * </p>
	 * 
	 * @param p_index
	 *            the index of P module used in the swirl
	 * @param p_modules
	 *            non-null. the P modules to use as excitation sources (usually
	 *            P module and the ones before). length == p_to_w.length ==
	 *            w_to_p[i].length. Can contain null values. p_modules[p_index]
	 *            cannot be null.
	 * @param p_to_w
	 *            non-null. the knowledge bases to transmit from the P modules
	 *            to the W module. p_to_w.length == w_to_p[i].length,
	 *            p_to_w[i].length == w_to_p.length
	 * @param w_to_p
	 *            non-null. the knowledge bases to transmit from the W modules
	 *            to the P module.
	 * @param w_index
	 *            in [0, w_to_p.length[
	 * @param w_module
	 *            non-null, the P module to use in the basic swirl
	 */
	protected static void basic_swirl(int p_index, Module[] p_modules,
			KnowledgeBase[][] p_to_w, KnowledgeBase[][] w_to_p, int w_index,
			Module w_module) {

		if (in(null, p_modules, p_modules[p_index], p_to_w, w_to_p, w_module)) {
			throw new NullPointerException(Arrays.toString( //
					ArrayTools.find_equals(null, p_modules, p_modules[p_index],
							p_to_w, w_to_p, w_module)));
		}
		if (!ArrayTools.all_equals(p_modules.length, p_to_w.length,
				w_to_p[0].length)) {
			throw new IllegalArgumentException("lengths:" + p_modules.length
					+ "," + p_to_w.length + "," + w_to_p[0].length);
		}
		if (p_to_w[0].length != w_to_p.length) {
			throw new IllegalArgumentException("lengths:" + p_to_w[0].length
					+ "," + w_to_p.length);
		}

		Module p_mod = p_modules[p_index];

		String[] p_exp = {};
		String[] old_p_exp = null;
		while (old_p_exp == null || p_exp.length != old_p_exp.length) {
			old_p_exp = p_exp;

			collect_all_input(p_modules, p_to_w, w_index, w_module);
			w_module.partial_confabulation(-1, false);

			transfer_excitation(w_module, w_to_p[w_index][p_index], p_mod);
			p_exp = p_mod.partial_confabulation(-1, false);
		}
	}

	/**
	 * Forces the P module to a conclusion after the different basic swirls.
	 * <p>
	 * first, input the excitations from the W modules used to the P module,
	 * then perform a WF on the P module
	 * </p>
	 * @param w_modules
	 *            non-null, the word modules used during the swirls. can contain
	 *            null values
	 * @param w_to_p
	 *            the kbs, non-null, rectangular
	 * @param p_index
	 *            index of the p module. Must be in [0, w_to_p[i].length[
	 * @param p_module
	 *            non-null, the P module
	 * 
	 * @return the final conclusion, or null if there was none
	 */
	protected static String after_swirls(Module[] w_modules, KnowledgeBase[][] w_to_p,
			int p_index, Module p_module) {
		if (in(null, p_module, w_to_p, w_modules)) {
			throw new NullPointerException(Arrays.toString( //
					ArrayTools.find_equals(null, p_module, w_to_p, w_modules)));
		}
		if (w_modules.length != w_to_p.length) {
			throw new IllegalArgumentException("lengths:" + w_modules.length
					+ "," + w_to_p.length);
		}

		collect_all_input(w_modules, w_to_p, p_index, p_module);
		return p_module.elementary_confabulation();
	}

	// TODO complete_n_words => implement "CT:TMT W4-W7 completion at once"
	// style
}
