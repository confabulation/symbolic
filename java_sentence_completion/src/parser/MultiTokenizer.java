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
