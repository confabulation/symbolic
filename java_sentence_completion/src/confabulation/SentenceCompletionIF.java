package confabulation;

/**
 * An interface to any sentence completion class
 * 
 * @author bernard
 * 
 */
public interface SentenceCompletionIF {

	/**
	 * Check whether given arguments are legal
	 * 
	 * @return "ok" if the argument can be passed to
	 *         {@link #next_word(String[], int)} or
	 *         {@link #next_expectation(String[], int)} without generating an
	 *         {@link IllegalArgumentException}. Otherwise, a diagnosis message
	 *         is returned.
	 */
	public String check_argument(String[] symbols, int index_to_complete);

	/**
	 * Find the word filling the first hole, or the next word
	 * 
	 * @param symbols
	 *            the symbol representation of the sentence. Special values:
	 *            <ul>
	 *            <li>null value indicates the absence of symbol at the given
	 *            position.
	 *            <li>empty string "" notes special empty symbol due to a
	 *            multi-word symbol
	 *            </ul>
	 *            <p>
	 *            non null
	 *            </p>
	 * @param index_to_complete
	 *            positive (>= 0), the position to fill in the symbols, negative
	 *            (< 0), request the implementation to detect the position to
	 *            complete.
	 *            <p>
	 *            Must correspond to a null position or an index outside the
	 *            <em>symbols</em> array.
	 *            </p>
	 *            <p>
	 *            An implementation-specific maximal value is possible, check
	 *            the arguments with {@link #check_argument(String[], int)}
	 *            </p>
	 * 
	 * @return the symbol found, or null if no word was found
	 * @throws IllegalArgumentException
	 *             when sentence completion class can't attempt the completion.
	 *             When that {@link IllegalArgumentException} is launched is
	 *             implementation-specific, but it can be checked with
	 *             {@link #check_argument(String[], int)}.
	 */
	public String next_word(String[] symbols, int index_to_complete)
			throws IllegalArgumentException;

	/**
	 * Find all the acceptable completions for the next word
	 * 
	 * @param index_to_complete
	 *            positive (>= 0), the position to fill in the symbols, negative
	 *            (< 0), request the implementation to detect the position to
	 *            complete.
	 *            <p>
	 *            Must correspond to a null position or an index outside the
	 *            <em>symbols</em> array.
	 *            </p>
	 * @param symbols
	 *            the symbol representation of the sentence. A null value simply
	 *            indicates the absence of symbol at the given position.
	 *            <p>
	 *            non null
	 *            </p>
	 * 
	 * @return the list of acceptable symbols. Can be empty if none was found
	 * @throws IllegalArgumentException
	 *             when sentence completion class can't attempt the completion.
	 *             When that {@link IllegalArgumentException} is launched is
	 *             implementation-specific, but it can be checked with
	 *             {@link #check_argument(String[], int)}.
	 */
	public String[] next_expectation(String[] symbols, int index_to_complete)
			throws IllegalArgumentException;

}