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

package com.github.confabulation.symbolic.parser;
import static com.github.confabulation.symbolic.utils.ArrayTools.in;
import com.github.confabulation.symbolic.utils.ArrayTools;

/**
 * A context-based tokenizer
 * 
 * <br/>
 * <p>
 * This tokenizer relies on <em>context patterns</em> to represent contexts.
 * </p>
 * <p>
 * A <em>context</em> pattern is an array of strings. It must contain a single
 * occurrence of CONTEXT_CENTER, a special string denoting the current word. It
 * can also contain null values, to represent "don't care" positions, and "*" to
 * match anything.
 * </p>
 * <p>
 * When extracting a context form a string, null values and CONTEXT_CENTER will
 * result in empty string "" subsymbols. "*" will either result the
 * corresponding word, or in the empty string subsymbol, if "*" is outside of
 * the given sentence.
 * </p>
 * 
 * @author bernard and cedric
 */
public class ContextTokenizer extends Tokenizer {
	public static final String CONTEXT_CENTER = "";
	private final String[] context;
	private int index_center;

	public ContextTokenizer(String[] context_pattern) {
		boolean[] checks = check_pattern(context_pattern);
		if (checks == null || checks.length == 0 || in(false, checks))
			throw new ContextSyntaxException(context_pattern);
		this.context = context_pattern;
		this.index_center = ArrayTools.find_equals(CONTEXT_CENTER,
				context_pattern)[0];
	}

	/**
	 * Checks whether a context pattern is valid
	 * 
	 * @param context
	 *            non-null, non-empty
	 * @return a list with the result of the check for each element of the
	 *         array, or null if the context does not have a CONTEXT_CENTER
	 */
	public static boolean[] check_pattern(String[] context) {
		boolean[] ret = new boolean[context.length];
		int n_center = 0;
		for (int i = 0; i < ret.length; i++) {
			if (context[i] == null) {
				ret[i] = true;
			} else if ("*".equals(context[i])) {
				ret[i] = true;
			} else if (CONTEXT_CENTER.equals(context[i]) && n_center <= 1) {
				n_center++;
				ret[i] = true;
			} else {
				ret[i] = false;
			}
		}
		if (n_center == 0) {
			return null;
		}
		return ret;
	}

	@Override
	public String[] words2symbols(String[] words) {
		String[] ret = new String[words.length];
		for (int i = 0; i < words.length; i++) {
			ret[i] = build_symbol_at(i, words, index_center, context);
		}
		return ret;
	}

	/**
	 * build a context symbol from matching <em>context</em> on <em>words</em>
	 * at position <em>pos</em>
	 * <p>
	 * </p>
	 * 
	 * @param pos
	 *            in [0, words.length -1]: position in the sentence
	 * @param words
	 *            non-null: the sentence
	 * @param index_context_center
	 *            in [0, context.length -1], the center of the context
	 * @param context
	 *            length > 0
	 * @return the symbol, something like 1(word0,,word2) for a context ["*",
	 *         CONTEXT_CENTER, "*"] and pos == 1
	 */
	public static String build_symbol_at(int pos, String[] words,
			int index_context_center, String[] context) {

		String symbol = index_context_center + "(";
		final String sep = " ";
		final String end = ")";

		// i is the index in String[] words
		int start, i;
		i = start = pos - index_context_center;
		while (i < 0) {
			symbol += sep;
			i++;
		}

		int end_common = Math.min(start + context.length, words.length);
		while (i < end_common) {
			if (context[i - start] == null
					|| context[i - start].equals(CONTEXT_CENTER)) {
				symbol += sep;
			} else if (context[i - start].equals("*")) {
				symbol += words[i] + sep;
			}
			i++;
		}

		while (i < start + context.length) {
			symbol += sep;
			i++;
		}
		// remove last separator
		return symbol.substring(0, symbol.length() - sep.length()) + end;
	}
}
