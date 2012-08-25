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

import utils.ArrayTools;

/**
 * A naive confabulation experiment only using links in the forward direction of
 * the sentence
 * 
 * @author bernard
 * 
 */
public class ForwardConfabulation extends ConfabulationStub {

	/**
	 * number of word modules
	 */
	private int n_modules;

	/**
	 * Do-nothing constructor, for extension purposes.
	 * <p>
	 * The child class must imperatively set
	 * </p>
	 */
	protected ForwardConfabulation(int n_modules) {
		this.n_modules = n_modules;
	}

	/**
	 * build single-level forward confabulation architecture and perform
	 * learning
	 * 
	 * @param n_modules
	 *            number of modules of the single level
	 * @param filename
	 *            corpus
	 * @throws IOException
	 *             if corpus can't be read
	 */
	public ForwardConfabulation(int n_modules, String filename)
			throws IOException {
		this(n_modules);

		// knowledge base specification
		boolean[][] kbs_spec = new boolean[n_modules][n_modules];

		// triangle so that any module before is linked to any module after
		for (int i = 0; i < n_modules; i++) {
			for (int j = i + 1; j < n_modules; j++) {
				kbs_spec[i][j] = true;
			}
		}
		init(kbs_spec, filename);
	}

	@Override
	public String check_argument(String[] symbols, int index_to_complete) {

		if (symbols == null) {
			return "symbols array can't be null";
		}
		if (symbols.length == 0) {
			return "symbols array has zero length";
		}

		if (index_to_complete >= 0) {
			// contains at least a symbol before index_to_complete
			if (ArrayTools.find_not_equal(null, symbols) >= index_to_complete) {
				return "symbols array does not contains any symbol before index_to_complete";
			}

			String check_index = check_index(symbols, index_to_complete);
			if (!"ok".equals(check_index)) {
				return check_index;
			}
			return check_vocabulary(symbols);
		}

		// autodetect mode
		int index = auto_index_to_complete(symbols);
		if (index < 0) {
			return check_index(symbols, naive_auto_index_to_complete(symbols));
		}

		// check detected index: it does not
		String check_index = check_index(symbols, index);
		if (!"ok".equals(check_index)) {
			return check_index;
		}
		return check_vocabulary(symbols);
	}

	/**
	 * Check whether index is ok
	 * 
	 * @return the error diagnosis, or "ok"
	 * @see ForwardConfabulation#auto_index_to_complete(String[])
	 */
	protected String check_index(String[] symbols, int index_to_complete) {
		if (index_to_complete < 0) {
			return "Negative index to complete";
		}
		if (index_to_complete >= n_modules) {
			return "no module for index " + index_to_complete + ".\n"
					+ "Support for indexes in [0, " + (n_modules - 1) + "].";
		}
		if (index_to_complete < symbols.length
				&& symbols[index_to_complete] != null) {
			return "Already a symbol (" + symbols[index_to_complete]
					+ ") at index " + index_to_complete;
		}
		return "ok";
	}

	@Override
	protected int auto_index_to_complete(String[] symbols) {
		int naive = naive_auto_index_to_complete(symbols);
		if (!"ok".equals(check_index(symbols, naive))) {
			return -1;
		}
		return naive;
	}

	/**
	 * detection of the index to complete without module existence check
	 * 
	 * @param symbols
	 *            non-null
	 * @return symbol.length, or the first index of a null element, or -1 if the
	 *         first element is null or the list is empty
	 * @see #check_index(String[], int)
	 */
	protected int naive_auto_index_to_complete(String[] symbols) {
		if (symbols.length == 0 || symbols[0] == null) {
			return -1;
		}
		int index = ArrayTools.find_equal(null, symbols);
		if (index >= 0) {
			return index;
		} else {
			// array full of symbols
			return symbols.length;
		}
	}
}
