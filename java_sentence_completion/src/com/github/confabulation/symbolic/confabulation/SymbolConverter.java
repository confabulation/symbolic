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

import java.util.Collection;
import java.util.regex.Pattern;

import com.github.confabulation.symbolic.utils.ArrayTools;

/**
 * Convert between the different representations of a symbol
 * 
 * @author bernard and cedric
 * 
 */
public class SymbolConverter {
	protected static final String SEP_MULTIWORD = " ";
	protected static final Pattern SEP_MULTIWORD_PATTERN = Pattern
			.compile(SEP_MULTIWORD);

	/**
	 * parse symbol array to symbol string
	 */
	public String string_symbol(String... str_array) {
		return ArrayTools.join(SEP_MULTIWORD, str_array);
	}

	/**
	 * parse symbol collection to symbol string
	 * 
	 * @see SymbolConverter#string_symbol(String[])
	 */
	public String string_symbol(Collection<String> c) {
		return string_symbol(c.toArray(new String[0]));
	}

	/**
	 * parse symbol string to symbol array
	 */
	public String[] str_array_symbol(String string_symbol) {
		return SEP_MULTIWORD_PATTERN.split(string_symbol);
	}

	/**
	 * number of symbols in the following string
	 */
	public int n_symbols(String multisymbol) {
		return str_array_symbol(multisymbol).length;
	}

}
