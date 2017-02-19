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

import com.github.confabulation.symbolic.parser.Tokenizer;

/**
 * multiconfabulation like the book, but on a single line
 * TODO
 * 
 * @author bernard and cedric
 * 
 */
public class Multiconfabulation2 extends Multiconfabulation1 {

	public Multiconfabulation2(int n_modules, String filename, Tokenizer tok)
			throws IOException {
		super(n_modules, filename, tok);
		// TODO generalize to any number of multiconfabulated modules
		n_multiconf = 3;
	}

	@Override
	protected String[] multiconf(String[] symbols, int index_to_complete) {
		// reproducing the body of the outer loop of
		// Confabulation#multiconf2(int, String)

		// compute first expectation, reproducing
		// Confabulation#activate_words(String, int, int, boolean))
		activate(symbols, mods);
		for (int i = index_to_complete; i < index_to_complete + n_multiconf; i++) {

			// get input from all set modules, except the ones under
			// multiconfabulation
			for (int src = 0; src < symbols.length; src++) {
				if (src < index_to_complete
						|| src >= index_to_complete + n_multiconf) {
					transfer_excitation(mods[src], kbs[src][i], mods[i]);
				}
			}
		}

		String[] result1 = stepA(index_to_complete, index_to_complete + 1,
				index_to_complete - 1, false, null);
		
		// TODO complete algo
		if (result1.length>1){
			
		}

		return symbols;
	}

	/**
	 * First step of the multiconfabulation process, swirling between 2 distinct
	 * modules (A and B) until either the first module has one solution, or the
	 * first module stop changing.
	 * 
	 * @param module1
	 * @param module2
	 * @param supportsize
	 * @param stepb
	 * @param list_expectation
	 * @return
	 * @throws ConfabulationException
	 */
	public String[] stepA(int module1, int module2, int supportsize,
			boolean stepb, String[] list_expectation)
			throws ConfabulationException {
		boolean first = true;
		String[] partial1 = new String[0];
		int previoussize = -1;

		if (module1 == module2) {
			throw new ConfabulationException(
					"Cannot execute multiconfabulation between 2 identical modules");
		}

		while (true) {

			previoussize = partial1.length;

			if (first) {
				if (stepb)
					partial1 = list_expectation;
				else
					partial1 = mods[module1].partial_confabulation(supportsize,
							false);
				mods[module2].partial_confabulation(supportsize, false);
			} else {
				partial1 = mods[module1].partial_confabulation(0, true);
				mods[module2].partial_confabulation(0, true);
			}

			if (partial1.length == previoussize || partial1.length <= 1)
				break;

			transfer_excitation(mods[module1], kbs[module1][module2],
					mods[module2]);
			transfer_excitation(mods[module2], kbs[module2][module1],
					mods[module1]);
		}
		return partial1;
	}

}
