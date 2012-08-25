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

import java.io.IOException;

import parser.Tokenizer;

/**
 * @author bernard
 * 
 */
public class Multiconfabulation1 extends FullMeshConfabulation {

	protected int n_multiconf;

	public Multiconfabulation1(int n_modules, String filename, Tokenizer tok)
			throws IOException {
		super(n_modules, filename);
		// TODO generalize to any number of multiconfabulated modules
		n_multiconf = 3;
	}

	@Override
	public String check_argument(String[] symbols, int index_to_complete) {
		String s = super.check_argument(symbols, index_to_complete);
		if (!"ok".equals(s)) {
			return s;
		}
		// locate index to complete
		int index = index_to_complete;
		if (index_to_complete < 0) {
			index = auto_index_to_complete(symbols);
		}

		// check enough modules, even with the additional modules needed for
		// multiconf
		if (index >= mods.length - n_multiconf) {
			return "the index to complete ("
					+ index_to_complete
					+ ") does not allow enough space to perform multiconfabulation. Maximum index to complete: "
					+ (mods.length - n_multiconf - 1);
		}

		// check the modules are available
		for (int i = index; i < Math.min(index + n_multiconf, symbols.length); i++) {
			if (symbols[i] != null) {
				return "Module "
						+ i
						+ "is needed to perform the multiconfabulation algorithm, "
						+ "but is occupied by symbol " + symbols[i];
			}
		}
		return "ok";
	}

	@Override
	public String[] next_expectation(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {
		String[] partial1 = multiconf(symbols, index_to_complete);
		clean();
		return partial1;
	}

	/**
	 * Performs a multiconfabulation on the module <em>index_to_complete</em>,
	 * using the two following to yield an expectation on the first one.
	 * 
	 * <p>
	 * The algorithm is designed to converge to a single active symbol in the
	 * first module. However, this outcome is not guaranteed.
	 * </p>
	 * 
	 * @param symbols
	 * @param index_to_complete
	 * @return the expectation
	 * @throws IllegalArgumentException
	 * @throws ConfabulationException
	 * @throws NullPointerException
	 */
	protected String[] multiconf(String[] symbols, int index_to_complete)
			throws IllegalArgumentException, ConfabulationException,
			NullPointerException {

		String check_result = check_argument(symbols, index_to_complete);
		if (!"ok".equals(check_result)) {
			throw new IllegalArgumentException(check_result);
		}

		// int n_size = activate_words(sentence, 1, number_of_words, true);
		activate(symbols, mods);

		int index;
		if (index_to_complete < 0) {
			index = auto_index_to_complete(symbols);
		} else {
			index = index_to_complete;
		}

		boolean first = true;
		String[] partial1 = new String[0];
		String[] partial2 = new String[0];
		String[] partial3 = new String[0];

		// initial expectations
		collect_all_input(mods, kbs, index, mods[index]);
		collect_all_input(mods, kbs, index + 1, mods[index]);
		collect_all_input(mods, kbs, index + 2, mods[index]);

		int mm = 0;
		while (mm < 1000) {
			// first compute expectation on both
			if (!first) {
				partial1 = mods[index].partial_confabulation(0, true);
				partial2 = mods[index + 1].partial_confabulation(0, true);
				partial3 = mods[index + 2].partial_confabulation(0, true);
			} else {
				partial1 = mods[index].partial_confabulation(index - 1, false);
				partial2 = mods[index + 1].partial_confabulation(index - 1,
						false);
				// TODO check: sure K == index -1 here?
				partial3 = mods[index + 2].partial_confabulation(index - 1,
						false);

				first = false;
			}
			// System.out.println("loop : "+mm+" - module1 exp : "+partial1.length+" - module2 exp : "+partial2.length+" - module3 exp : "+partial3.length);

			// check if answer is equal to one
			if (partial1.length <= 1 && partial2.length <= 1
					&& partial3.length <= 1)
				break;
			else {
				mm++;
			}

			// TODO replace by transfer_excitation()
			mods[index].add_excitations(kbs[index + 1][index]
					.transmit(mods[index + 1].getNormalised_excitations()));
			mods[index].add_excitations(kbs[index + 2][index]
					.transmit(mods[index + 2].getNormalised_excitations()));

			mods[index].add_excitations(kbs[index][index + 1]
					.transmit(mods[index].getNormalised_excitations()));
			mods[index].add_excitations(kbs[index + 2][index + 1]
					.transmit(mods[index + 2].getNormalised_excitations()));

			mods[index].add_excitations(kbs[index][index + 2]
					.transmit(mods[index].getNormalised_excitations()));
			mods[index].add_excitations(kbs[index + 1][index + 2]
					.transmit(mods[index + 1].getNormalised_excitations()));
		}

		// if (partial1.length > 1 || partial2.length > 1 || partial3.length >
		// 1) {
		// for (int i = 0; i < partial1.length; i++) {
		// System.out.println("answer module 1 : " + partial1[i]);
		// }
		// for (int i = 0; i < partial2.length; i++) {
		// System.out.println("answer module 2 : " + partial2[i]);
		// }
		// for (int i = 0; i < partial3.length; i++) {
		// System.out.println("answer module 3 : " + partial3[i]);
		// }
		// }
		// System.out.println(sentence + " " + partial1[0] + " " + partial2[0]
		// + " " + partial3[0]);
		return partial1;
	}

	@Override
	public String next_word(String[] symbols, int index_to_complete)
			throws IllegalArgumentException {
		multiconf(symbols, index_to_complete);

		int index;
		if (index_to_complete < 0) {
			index = auto_index_to_complete(symbols);
		} else {
			index = index_to_complete;
		}

		// force a single solution
		String ret = mods[index].elementary_confabulation();

		clean();
		return ret;
	}
}
