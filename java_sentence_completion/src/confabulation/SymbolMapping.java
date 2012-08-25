package confabulation;

import java.util.HashMap;
import java.util.Set;

/**
 * define a mappring between symbols and list indexes
 * 
 * @author cedric
 */
public class SymbolMapping {

	HashMap<String, Integer> symbtoint;
	HashMap<Integer, String> inttosymb;

	public SymbolMapping() {
		symbtoint = new HashMap<String, Integer>();
		inttosymb = new HashMap<Integer, String>();
	}

	/**
	 * insert a new symbol in the mapping
	 * 
	 * @param symbol
	 *            non null
	 */
	public void add_symbol(String symbol) {
		if (!symbtoint.containsKey(symbol)) {
			int symbol_index = symbtoint.size();
			symbtoint.put(symbol, symbol_index);
			inttosymb.put(symbol_index, symbol);
		}
	}

	/**
	 * test if a symbol is already registered
	 * 
	 * @param symbol
	 * @return True if the symbol already is in the mapping
	 */
	public boolean contains(String symbol) {
		return symbtoint.containsKey(symbol);
	}

	/**
	 * get the index of the symbol
	 * 
	 * @param symbol
	 * @return the index, if the symbol is present.
	 * @throws ConfabulationException
	 *             when the symbol is not found
	 */
	public int index_of(String symbol) throws ConfabulationException {
		if (symbtoint.containsKey(symbol)) {
			return symbtoint.get(symbol);

		}
		String error = "The word '" + symbol + "' wasn't found in the corpus.";
		throw new ConfabulationException(error);
	}

	/**
	 * Find the symbol corresponding to index
	 * 
	 * @param index
	 * @return the symbol corresponding to the index, which can be null
	 * @throws ConfabulationException
	 *             when the symbol is not found
	 */
	public String get_symbol(int index) throws ConfabulationException {
		// no need for a contains(): we know the symbols are only added and that
		// they are numbered starting from zero, according to the order of
		// insertion
		if (0 <= index && index < size()) {
			return inttosymb.get(index);
		}
		throw new ConfabulationException("Words mapping does not know index("
				+ index + "). It is expected to be in [0, " + size() + "[");
	}

	/**
	 * @return the number of registered symbols
	 */
	public int size() {
		return symbtoint.size();
	}

	@Override
	public String toString() {
		return "WordsMapping [wordtoint=" + symbtoint + "]";
	}
	
	/**
	 * all the symbols of the word mapping
	 */
	public Set<String> get_all_symbols(){
		return symbtoint.keySet();
	}

}