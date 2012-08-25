/**
 * 
 */
package parser;

import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import utils.ArrayTools;

/**
 * @author bernard
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
