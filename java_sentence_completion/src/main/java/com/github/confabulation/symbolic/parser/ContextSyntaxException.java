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

/**
 * 
 */
package com.github.confabulation.symbolic.parser;

import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import com.github.confabulation.symbolic.utils.ArrayTools;

/**
 * @author bernard and cedric
 *
 */
public class ContextSyntaxException extends PatternSyntaxException {

	private static final long serialVersionUID = -7271373570534907353L;

	/**
	 * Find where context pattern went wrong
	 * @param context the context pattern {@link ContextTokenizer}
	 */
	public ContextSyntaxException(String[] context) {
		super(gen_desc(context), Arrays.toString(context),-1);
		// TODO : visual indication of errors in context.
		// either change -1 or do our own
	}

	private static String gen_desc(String[] context) {
		int[] wrongs = ArrayTools.find_equals(false,
				ContextTokenizer.check_pattern(context));
		return "Error at indexes " + Arrays.toString(wrongs)
				+ "of context pattern";
	}
}
