package parser;

/**
 * Wrapper class to transform a Tokenizer into a MultiTokenizer
 * 
 * @author bernard
 */
public class TokenizerWrapper implements MultiTokenizer {

	private Tokenizer[] toks;

	/**
	 * TokenizerWrapper(new Tokenizer[]{tok})
	 * 
	 * @see #TokenizerWrapper(Tokenizer[])
	 */
	public TokenizerWrapper(Tokenizer tok) {
		this(new Tokenizer[] { tok });
	}

	/**
	 * Wraps the different tokenizers into a single {@link MultiTokenizer}
	 */
	public TokenizerWrapper(Tokenizer... toks) {
		this.toks = toks;
	}

	/* see interface doc */
	@Override
	public String[][] words2symbols(String[] words) {
		String[][] ret = new String[words.length][toks.length];

		for (int t = 0; t < toks.length; t++) {
			String[] symbols = toks[t].words2symbols(words);
			for (int i = 0; i < symbols.length; i++) {
				ret[i][t] = symbols[i];
			}
		}
		return ret;
	}

}
