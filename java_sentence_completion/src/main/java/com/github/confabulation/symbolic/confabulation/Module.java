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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.confabulation.symbolic.sparse.float_.DOK1Dfloat;
import com.github.confabulation.symbolic.sparse.int_.DOK1Dint;
import com.github.confabulation.symbolic.utils.MathTools;

/**
 * Excitations levels of the symbols implemented by a module
 * <p>
 * This class is basically a list of excitations linked with a WordsMapping.
 * However, additional state: frozen (or not) is required to implement the
 * {@link #freeze()} operation
 * </p>
 * 
 * @author cedric & bernard
 * 
 */
public class Module {

	/**
	 * The excitations (not normalized).
	 */
	protected DOK1Dfloat excitations;

	/**
	 * Count of the number of knowledge base inputs by symbol.
	 * <p>
	 * This is due to the fact that formula for multiple excited source symbols
	 * (CT:TMT p.128) breaks the guarantee that the excitation of a symbol is in
	 * [N {@link ConfConstant#B}, N*({@link ConfConstant#B} + ln(1/
	 * {@link ConfConstant#pzero}))
	 * </p>
	 */
	protected DOK1Dint kb_inputs;

	/**
	 * The {@link #excitations}, in normalised form.
	 * <p>
	 * This variable is only set when accessed, and is freed on operations that
	 * modify the excitations in bandwidth format (like elementary
	 * confabulation, WK, CK, ...)
	 * </p>
	 */
	protected DOK1Dfloat normalised_excitations;

	/**
	 * subset of the potentially winning symbols in a multiconfabulation, i.e.
	 * the expectations
	 * 
	 * @see #freeze()
	 * @see #excitations
	 */
	protected int[] frozen_indexes;

	/**
	 * the table of the symbols and their indexes in this module
	 * <p>
	 * public fields is not best practice, but having a getSymbolMapping(){
	 * return sm; } is as bad as this in encapsulation and uglier since it's
	 * useless code... would be cool to have a read-only interface to this
	 * object
	 * </p>
	 */
	public final SymbolMapping sm;

	public Module(SymbolMapping sm) {
		this.sm = sm;
		reset();
	}

	/**
	 * (E) Erases the excitation states of the module
	 * <p>
	 * Unfreezes module
	 * </p>
	 */
	public void reset() {
		excitations_to_zero();
		unfreeze();
	}

	/**
	 * Zeroes the excitations
	 * <p>
	 * Does not unfreeze module
	 * </p>
	 */
	public void excitations_to_zero() {
		normalised_excitations = null;
		excitations = new DOK1Dfloat(sm.size());
		kb_inputs = new DOK1Dint(sm.size());
	}

	/**
	 * Activate a given word in the module, with the strength of K knowledge
	 * links. This doesn't change the frozen state.
	 * 
	 * @param word
	 *            the word to activate (must be in the already active symbols if
	 *            the module is frozen)
	 * @param K
	 *            > 0. the activation strength, in number of KnowledgeLink
	 *            inputs
	 * @throws ConfabulationException
	 * @see #freeze()
	 */
	public void activate_word(String word, int K)
			throws ConfabulationException, IllegalArgumentException {

		normalised_excitations = null;

		if (K < 0) {
			throw new IllegalArgumentException("K < 0:" + K);
		}

		int index = sm.index_of(word);
		if (is_frozen()) {
			// TODO use set of frozen indexes, instead of array
			for (int i : frozen_indexes) {
				if (index == i) {
					excitations.set(index, K * ConfConstant.B);
					kb_inputs.set(index, K);
					return;
				}
			}
		} else {
			excitations.set(index, K * ConfConstant.B);
			kb_inputs.set(index, K);
		}
	}

	/**
	 * add K excitation levels to each excited symbol
	 * <p>
	 * If K is negative, the excitations are decreased but don't go below zero.
	 * </p>
	 * <p>
	 * UNUSED
	 * </p>
	 * 
	 * @param K
	 *            number of excitation levels to add/remove
	 */
	public void add_to_all_excited(int K) {
		normalised_excitations = null;
		if (is_frozen()) {
			for (int i : frozen_indexes) {

				float val = MathTools.ramp_fun(excitations.get(i) + K
						* ConfConstant.B);
				excitations.set(i, val);

				int n_inputs = MathTools.ramp_fun(kb_inputs.get(i) + K);
				kb_inputs.set(i, n_inputs);
			}
		} else {
			for (Entry<Integer, Float> e : excitations.nz_elements()) {
				int i = e.getKey();

				float val = MathTools.ramp_fun(e.getValue() + K
						* ConfConstant.B);
				excitations.set(i, val);

				int n_inputs = MathTools.ramp_fun(kb_inputs.get(i) + K);
				kb_inputs.set(i, n_inputs);
			}
		}
	}

	/**
	 * Add value to the excitation present at index, if the module is not
	 * frozen. <br/>
	 * If it is, the index must be in the already active symbols
	 * <p>
	 * UNUSED
	 * </p>
	 * 
	 * @param index
	 * @param value
	 *            can be negative or positive. If negative, if the sum is below
	 *            0, the result is set to 0
	 * @see #freeze()
	 */
	public void add_to_index(int index, float value) {
		normalised_excitations = null;
		if (is_frozen()) {
			for (int i : frozen_indexes) {

				if (index == i) {
					float previous_val = excitations.get(index);
					excitations.set(index,
							MathTools.ramp_fun(previous_val + value));

					int n_inputs = (int) (value / ConfConstant.B)
							* ConfConstant.B;
					kb_inputs.set(i, n_inputs + kb_inputs.get(i));
					return;
				}
			}
		} else {
			float previous_val = excitations.get(index);
			excitations.set(index, MathTools.ramp_fun(previous_val + value));

			int n_inputs = (int) (value / ConfConstant.B) * ConfConstant.B;
			kb_inputs.set(index, n_inputs + kb_inputs.get(index));
		}
	}

	/**
	 * (W F) Perform an elementary confabulation which returns the word with the
	 * maximum excitation value. No normalisation is performed.
	 * 
	 * @post the Module is frozen again (only the winning symbol is a possible
	 *       conclusion)
	 * @return the winning symbol of this confabulation, or null if no symbol
	 *         was found
	 */
	public String elementary_confabulation() {
		return elementary_confabulation(1);
	}

	/**
	 * (WK F) Elementary confabulation with at least K knowledge link inputs <br/>
	 * <br/>
	 * 
	 * @post the Module is frozen (only the winning symbol is a possible
	 *       conclusion)
	 * @param K
	 *            the minimal number of knowledge link inputs. If -1, take the
	 *            maximum number possible; -2, at least the maximum -1
	 * @return the word with the maximum excitation value and at least K
	 *         knowledge link inputs. If no word corresponds, return null
	 * @see Module#freeze()
	 */
	public String elementary_confabulation(int K) {
		normalised_excitations = null;

		// compute K if negative
		K = actual_K(K);

		Set<Entry<Integer, Float>> nz_excit = excitations.nz_elements();
		Set<Entry<Integer, Float>> min_K_input = excitations_above(K, nz_excit);
		Entry<Integer, Float> max = max_excitations(min_K_input);

		int max_index = -1;
		int n_inputs_max = -1;
		if (max != null) {
			max_index = max.getKey();
			n_inputs_max = kb_inputs.get(max_index);
		}

		excitations_to_zero();

		if (max == null) {
			freeze();
			return null;
		}

		// activate only the best word found
		excitations.set(max_index, max.getValue());
		kb_inputs.set(max_index, n_inputs_max);

		freeze();
		return sm.get_symbol(max_index);
	}

	/**
	 * (CK F) Confabulation conclusions having at least K knowledge link inputs <br/>
	 * book page 141 <br/>
	 * Does not normalise the excitation levels If used with multiconf parameter
	 * set to true, the result will be all the symbols with excitation level
	 * between the symbol having the maximum excitation and that maximum minus B
	 * 
	 * @post the Module is frozen (less symbols are potential winners)
	 * @param K
	 *            if multiconf is false
	 *            <ul>
	 *            <li>if >= 0: the minimal number of knowledge link input needed
	 *            </li>
	 *            <li>if < 0 : only keep excitations that are in the top |K|
	 *            levels</li>
	 *            </ul>
	 * @param multiconf
	 *            if true, change the threshold computation to the maximal
	 *            excitation - B (midl tightening, CT:TMT p.129)
	 * @return the set of expectations, i.e. potential winning symbols
	 * @see Module#freeze()
	 */
	public String[] partial_confabulation(int K, boolean multiconf) {
		normalised_excitations = null;

		List<Entry<Integer, Float>> expectations = new LinkedList<Entry<Integer, Float>>();

		if (!multiconf) {

			K = actual_K(K);
			Set<Entry<Integer, Float>> nz_excit = excitations.nz_elements();
			expectations = new LinkedList<Entry<Integer, Float>>(
					excitations_above(K, nz_excit));

		} else {
			// we are in the multiconfabulation case, so the formula is a bit
			// different,
			// if the max excitation level is less than B, we keep everything,
			// otherwise
			// the threshold of value to keep are all the values >= (max-B)
			Entry<Integer, Float> max = max_excitations(excitations
					.nz_elements());
			if (max == null) {
				freeze();
				return new String[0];
			}
			float threshold = Math.max(max.getValue() - ConfConstant.B, 0);

			// get each symbol excited above threshold
			for (Entry<Integer, Float> e : excitations.nz_elements()) {
				if (e.getValue() > threshold) {
					expectations.add(e);
				}
			}
		}

		DOK1Dint kb_inputs_temp = new DOK1Dint(kb_inputs);

		reset();

		String[] ret = new String[expectations.size()];
		int ind_ret = 0;

		// put back excitations & find symbols corresponding to the indexes
		for (Entry<Integer, Float> e : expectations) {

			int index = e.getKey();
			excitations.set(index, e.getValue());
			kb_inputs.set(index, kb_inputs_temp.get(index));

			ret[ind_ret] = sm.get_symbol(index);
			ind_ret += 1;
		}
		freeze();
		return ret;
	}

	/**
	 * (F) Freeze the module: no symbol outside those already excited is allowed
	 * to become excited.
	 * 
	 * @see #unfreeze()
	 * @see #is_frozen()
	 */
	public void freeze() {
		frozen_indexes = new int[excitations.nnz()];
		int ind_frozen = 0;
		for (Entry<Integer, Float> e : excitations.nz_elements()) {
			frozen_indexes[ind_frozen] = e.getKey();
			ind_frozen += 1;
		}
	}

	/**
	 * Cancel the frozen state
	 * 
	 * @see #freeze()
	 * @see #is_frozen()
	 */
	public void unfreeze() {
		frozen_indexes = null;
	}

	/**
	 * indicate the frozen state
	 * 
	 * @return whether the module is under the effect of a freeze()
	 * @see #freeze()
	 * @see #unfreeze()
	 */
	public boolean is_frozen() {
		return frozen_indexes != null;
	}

	/**
	 * @return the normalised excitations
	 */
	public DOK1Dfloat getNormalised_excitations() {

		if (normalised_excitations != null) {
			return normalised_excitations;
		}
		normalised_excitations = new DOK1Dfloat(sm.size());

		double sum = 0;
		for (Entry<Integer, Float> e : excitations.nz_elements()) {
			sum += e.getValue();
		}

		for (Entry<Integer, Float> e : excitations.nz_elements()) {
			normalised_excitations
					.set(e.getKey(), (float) (e.getValue() / sum));
		}
		return normalised_excitations;
	}

	/**
	 * Add non-normalised excitations to the current excitation of the module
	 * 
	 * @param input
	 *            non-normalised excitations (sums of link strength by
	 *            normalised excitation products), non-null
	 */
	public void add_excitations(DOK1Dfloat input) {

		normalised_excitations = null;
		if (is_frozen()) {
			for (int i : frozen_indexes) {
				float kl_input = input.get(i);
				if (kl_input > 0) {
					excitations.set(i, input.get(i) + kl_input);
					kb_inputs.set(i, kb_inputs.get(i) + 1);
				}
			}
		} else {
			excitations.add(input);
			for (Entry<Integer, Float> e : input.nz_elements()) {
				int i = e.getKey();
				kb_inputs.set(i, kb_inputs.get(i) + 1);
			}
		}
	}

	/**
	 * get the expectation
	 * 
	 * @return the current active symbols, the empty lyst of there is none
	 */
	public String[] get_expectation() {
		String[] ret = new String[excitations.nnz()];
		int ind_ret = 0;
		for (Entry<Integer, Float> e : excitations.nz_elements()) {

			int index = e.getKey();
			ret[ind_ret] = sm.get_symbol(index);
			ind_ret++;
		}
		return ret;
	}

	/**
	 * Return the maximal excitation from the given entries
	 * 
	 * @param entries
	 *            non-null
	 * @return
	 */
	protected static Entry<Integer, Float> max_excitations(
			Iterable<Entry<Integer, Float>> entries) {
		Entry<Integer, Float> max = null;
		float max_value = 0;

		for (Entry<Integer, Float> e : entries) {
			if (max == null) {
				max = e;
				max_value = e.getValue();
			} else if (e.getValue() > max_value) {
				max = e;
				max_value = e.getValue();
			}
		}
		return max;
	}

	/**
	 * Return each pair (index, excitation) which receives at least K knowledge
	 * base inputs
	 * 
	 * @param K
	 * @param nz_excitations
	 *            non-null, should only contain indexes of the excitation vector
	 * @return
	 */
	protected Set<Entry<Integer, Float>> excitations_above(int K,
			Set<Entry<Integer, Float>> nz_excitations) {

		Set<Entry<Integer, Float>> ret = new HashSet<Entry<Integer, Float>>();
		for (Entry<Integer, Float> e : nz_excitations) {
			if (kb_inputs.get(e.getKey()) >= K) {
				ret.add(e);
			}
		}
		return ret;
	}

	/**
	 * compute K if negative
	 * 
	 * @param K
	 * @return if K is positive, K.
	 *         <p>
	 *         Otherwise return max_K() + 1 + K, in other words, if K == -1,
	 *         return the maximum number of knowledge base input of this
	 *         excitation
	 *         </p>
	 */
	protected int actual_K(int K) {
		if (K >= 0) {
			return K;
		}
		return max_K() + 1 + K;
	}

	/**
	 * @return the maximal number of knowledge base input a symbol receives
	 */
	protected int max_K() {
		int max_K = 0;
		for (Entry<Integer, Integer> e : kb_inputs.nz_elements()) {
			int val = e.getValue();
			if (val > max_K) {
				max_K = val;
			}
		}
		return max_K;
	}
}
