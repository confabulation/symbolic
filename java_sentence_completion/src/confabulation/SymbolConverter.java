package confabulation;

import java.util.Collection;
import java.util.regex.Pattern;

import utils.ArrayTools;

/**
 * Convert between the different representations of a symbol
 * 
 * @author bernard
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
