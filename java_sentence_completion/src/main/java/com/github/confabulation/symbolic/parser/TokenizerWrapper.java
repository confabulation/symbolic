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

/**
 * Wrapper class to transform a Tokenizer into a MultiTokenizer
 * 
 * @author bernard and cedric
 */
public class TokenizerWrapper implements MultiTokenizer {

	private Tokenizer[] toks;

	/**
	 * TokenizerWrapper(new Tokenizer[]{tok})
	 * 
	 * @see #TokenizerWrapper(Tokenizer[])
	 */
	public TokenizerWrapper(Tokenizer tok) {
		this(new Tokenizer[] { tok });
	}

	/**
	 * Wraps the different tokenizers into a single {@link MultiTokenizer}
	 */
	public TokenizerWrapper(Tokenizer... toks) {
		this.toks = toks;
	}

	/* see interface doc */
	@Override
	public String[][] words2symbols(String[] words) {
		String[][] ret = new String[words.length][toks.length];

		for (int t = 0; t < toks.length; t++) {
			String[] symbols = toks[t].words2symbols(words);
			for (int i = 0; i < symbols.length; i++) {
				ret[i][t] = symbols[i];
			}
		}
		return ret;
	}

}
