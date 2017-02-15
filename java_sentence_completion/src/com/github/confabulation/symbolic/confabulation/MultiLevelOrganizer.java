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

import static com.github.confabulation.symbolic.utils.ArrayTools.in;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.github.confabulation.symbolic.parser.SymbolOrganizer;
import com.github.confabulation.symbolic.utils.ArrayTools;
import com.github.confabulation.symbolic.utils.HashStandardTrie;

/**
 * Organizer for the two-level sentence completion architecture
 * <p>
 * Beware: still does not support the multisymbol case, even though the
 * interface supports it.</p>
 * <p> Should either reimplement a new one, or add parameters
 * to this to specify if should learn all the symbols of the level below, as for
 * the context module of CT:TMT chap 6.A
 * </p>
 * TODO support context module
 * 
 * @author bernard and cedric
 * 
 */
public class MultiLevelOrganizer implements SymbolOrganizer {

	protected int[] level_sizes;

	protected SymbolConverter[] sy_convs;

	protected Vector<HashStandardTrie<String>> tries;

	/**
	 * builds the multilevel organizer
	 * 
	 * @param level_sizes
	 *            the sizes of the different levels. Each element must be
	 *            positive
	 * @param mappings
	 *            the list of symbol mappings for each level. Must have
	 *            Mappings.length == level_sizes.length
	 * @param sy_convs
	 *            symbol converters for each of the levels. must have
	 *            sy_convs.length == mappings.length == level_sizes.length
	 */
	public MultiLevelOrganizer(int[] level_sizes, SymbolMapping[] mappings,
			SymbolConverter[] sy_convs) {
		// checks
		if (in((Object) null, level_sizes, mappings, sy_convs)) {

			String[] nulls = ArrayTools.get_map(null, //
					new Object[] { level_sizes, mappings, sy_convs }, //
					new String[] { "level_sizes", "mappings", "sy_convs" });
			throw new NullPointerException("null: "
					+ ArrayTools.join(", ", nulls));
		}
		if (level_sizes.length != mappings.length
				|| mappings.length != sy_convs.length) {
			throw new IllegalArgumentException("lengths (" + level_sizes.length
					+ "," + mappings.length + ") non-equal");
		}

		// fill the Trie of each level
		tries = new Vector<HashStandardTrie<String>>();
		for (int i = 0; i < sy_convs.length; i++) {
			HashStandardTrie<String> trie = new HashStandardTrie<String>();

			// parse each symbol and add the subsymbols to the Trie
			for (String symbol : mappings[i].get_all_symbols()) {
				trie.add(Arrays.asList(sy_convs[i].str_array_symbol(symbol)));
			}

			tries.add(trie);
		}
		this.level_sizes = level_sizes;
		this.sy_convs = sy_convs;
	}

	/**
	 * @throws ConfabulationException
	 *             when trie fails to find a match for some symbols (trie should
	 *             find one since it contains all the symbols, including the
	 *             single words symbols)
	 */
	@Override
	public String[][] organize(String[] symbols) throws ConfabulationException {

		int n_levels = level_sizes.length;
		String[][] levels = new String[n_levels][];
		for (int i = 0; i < n_levels; i++) {

			String[] level = levels[i] = new String[level_sizes[i]];
			HashStandardTrie<String> trie = tries.get(i);
			LinkedList<String> symbs = new LinkedList<String>(
					Arrays.asList(symbols));

			// find longuest match and remove matched symbols
			int j = 0;
			while (j < level.length && !symbs.isEmpty()) {

				List<String> match = trie.find_longest(symbs);

				if (match.isEmpty()) {
					throw new ConfabulationException("No match for symbols "
							+ symbols + " at position " + j + ", level " + i);
					// TODO create & use SymbolNotFoundException
				}

				// store found multisymbol
				level[j] = sy_convs[i].string_symbol(match);

				int end = Math.min(level.length, j + match.size());
				while (j < end) {
					symbs.remove();
					// level[j] = null; // no need, default
					j++;
				}
			}
		}
		return ArrayTools.zip(ArrayTools.concat(levels));
	}
}
