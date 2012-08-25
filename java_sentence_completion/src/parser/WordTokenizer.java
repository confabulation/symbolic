/**
 * 
 */
package parser;

/**
 * Trivial tokenizer: returns the words
 * 
 * @author bernard
 * 
 */
public class WordTokenizer extends Tokenizer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.Tokenizer#words2symbols(java.lang.String[])
	 */
	@Override
	public String[] words2symbols(String[] words) {
		return words;
	}

}
