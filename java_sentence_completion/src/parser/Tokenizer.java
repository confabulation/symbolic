package parser;

import java.util.regex.Pattern;

import utils.ArrayTools;

/**
 * basic symbol parsing
 * 
 * @author bernard
 * 
 */
public abstract class Tokenizer {
	/**
	 * convert a sentence presented as words into symbols
	 * 
	 * @param words
	 *            can contain empty strings, or be null
	 * @return one symbol per word, in the same order. null means no symbol for
	 *         the word (can be used to deal with multi-word symbols)
	 *         <p>
	 *         <strong>Condition:</strong>
	 *         </p>
	 *         <p>
	 *         words == return == null || return.length == words.length
	 *         </p>
	 */
	public abstract String[] words2symbols(String[] words);

	/**
	 * parse <em>line</em> into symbols, using <em>separator_regex</em> to
	 * separate the words. Empty words "" are ignored
	 * 
	 * @param line
	 *            can be null
	 * @param separator_regex
	 *            non-null, well-formed regex for the separator
	 * @return one symbol per word, in the same order, null if line is null
	 * @see #words2symbols(String[])
	 */
	public String[] parse(String line, String separator_regex) {
		return parse(line, Pattern.compile(separator_regex));
	}

	/**
	 * Core of {@link #parse(String, String)}, for optimisation
	 * 
	 * @see #parse(String, String)
	 * @see #words2symbols(String[])
	 */
	public String[] parse(String line, Pattern separator_pattern) {
		if (line == null) {
			return null;
		}
		String[] temp = ArrayTools.removeEvery("",
				separator_pattern.split(line));

		return words2symbols(temp);
	}
}
