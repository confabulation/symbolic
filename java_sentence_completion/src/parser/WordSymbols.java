/**
 * 
 */
package parser;

import java.util.Arrays;

import utils.ArrayTools;

/**
 * @author bernard
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
