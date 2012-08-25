/**
 * 
 */
package parser;

/**
 * arrange, duplicate, re-represent symbols for the learning
 * 
 * @author bernard
 * 
 */
public interface SymbolOrganizer {

	/**
	 * Put symbols at the right place for each module, so that the knowledge
	 * bases can perform learning
	 * 
	 * @param symbols
	 *            symbols as parsed by {@link Tokenizer}
	 * @return a list of all the possible symbols for each of the modules.
	 *         <p>
	 *         Each symbol in each list will be associated with each symbol in
	 *         the other lists, if there is a knowledge base between the
	 *         corresponding modules.
	 *         </p>
	 *         <p>
	 *         If the position contains null or the empty list or a list
	 *         containing only null symbols, no association will be learned in
	 *         the knowledge bases connected with the corresponding module
	 *         </p>
	 */
	String[][] organize(String[] symbols);
}
