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
package parser;

import java.util.Arrays;

import utils.ArrayTools;

/**
 * @author bernard and cedric
 *
 */
public class WordSymbols implements SymbolOrganizer {

	protected int n_mods;
	
	public WordSymbols(int n_modules){
		n_mods = n_modules;
	}
	
	@Override
	public String[][] organize(String[] symbols) {
		String[] good_size = Arrays.copyOf(symbols, n_mods);
		return ArrayTools.zip(good_size);
	}

}
