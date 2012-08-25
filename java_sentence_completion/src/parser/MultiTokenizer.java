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

package parser;

public interface MultiTokenizer {

	/**
	 * convert a sentence presented as words into symbols
	 * 
	 * @param words
	 * @return the symbols extracted from this string, in the same order. There
	 *         can be multiple symbols for a single word, hence a list of lists of symbols
	 * 			<p>return.length == words.length</p>
	 */
	String[][] words2symbols(String[] words);
}
