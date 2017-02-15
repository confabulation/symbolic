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

/**
 * arrange, duplicate, re-represent symbols for the learning
 * 
 * @author bernard and cedric
 * 
 */
public interface SymbolOrganizer {

	/**
	 * Put symbols at the right place for each module, so that the knowledge
	 * bases can perform learning
	 * 
	 * @param symbols
	 *            symbols as parsed by {@link Tokenizer}
	 * @return a list of all the possible symbols for each of the modules.
	 *         <p>
	 *         Each symbol in each list will be associated with each symbol in
	 *         the other lists, if there is a knowledge base between the
	 *         corresponding modules.
	 *         </p>
	 *         <p>
	 *         If the position contains null or the empty list or a list
	 *         containing only null symbols, no association will be learned in
	 *         the knowledge bases connected with the corresponding module
	 *         </p>
	 */
	String[][] organize(String[] symbols);
}
