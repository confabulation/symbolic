package confabulation;

import java.util.Map.Entry;

import sparse.Pair;
import sparse.float_.CSC2Dfloat;
import sparse.float_.DOK1Dfloat;
import sparse.float_.DOK2Dfloat;
import sparse.int_.DOK2Dint;

/**
 * A knowledge base
 * 
 * @author bernard & Cédric
 * 
 */
public class KnowledgeBase {

	/**
	 * The co-occurrence count matrix. <br/>
	 * Internally it is stored as<br/>
	 * columns <-> source symbols <br/>
	 * rows <-> target symbols <br/>
	 */
	protected DOK2Dint cooccurrence_counts;

	/**
	 * The knowledge base. Internally it is stored as<br/>
	 * columns <-> source symbols <br/>
	 * rows <-> target symbols <br/>
	 */
	protected CSC2Dfloat kbase;

	private int[] row_count;
	public final String name;
	private SymbolMapping targ_map;
	private SymbolMapping src_map;

	/**
	 * Build a new Knowledge base.
	 * 
	 * @param name
	 *            a convenient name of this knowledge base
	 * @param src_map
	 *            non-null
	 * @param targ_map
	 *            non-null
	 */
	public KnowledgeBase(String name, SymbolMapping src_map,
			SymbolMapping targ_map) {
		this.name = name;
		this.src_map = src_map;
		this.targ_map = targ_map;
		cooccurrence_counts = new DOK2Dint(targ_map.size(), src_map.size());
		// init links to 0
		kbase = new CSC2Dfloat(new DOK2Dfloat(targ_map.size(), src_map.size()));
		row_count = new int[targ_map.size()];
	}

	public KnowledgeBase(SymbolMapping src_map, SymbolMapping targ_map) {
		this("", src_map, targ_map);
	}

	/**
	 * add one to the count of word co-occurences
	 * 
	 * @pre must be in occurence_count state
	 * @param src_symbol
	 * @param targ_symbol
	 * @throws ConfabulationException
	 */
	public void add(String src_symbol, String targ_symbol)
			throws ConfabulationException {

		int row = targ_map.index_of(targ_symbol);
		int col = src_map.index_of(src_symbol);

		add(row, col);
	}

	/**
	 * add one to the count of word co-occurences
	 */
	protected void add(int targ_symbol, int src_symbol) {
		kbase = null; // discard out of date version
		row_count[targ_symbol]++;
		int previous_count = cooccurrence_counts.get(targ_symbol, src_symbol);
		cooccurrence_counts.set(targ_symbol, src_symbol, previous_count + 1);
	}

	/**
	 * Compute the link strengths
	 */
	public void compute_link_strengths() {
		if (kbase != null) {
			return;
		}
		DOK2Dfloat link_strengths = new DOK2Dfloat(
				cooccurrence_counts.nlines(), cooccurrence_counts.ncols());
		// compute strengths
		for (Entry<Pair<Integer, Integer>, Integer> e : cooccurrence_counts
				.nz_elements()) {

			Pair<Integer, Integer> coord = e.getKey();
			Integer l = coord.first;
			Integer c = coord.second;

			link_strengths.set(l, c, link_strength(e.getValue()
					/ (float) row_count[l]));
		}

		// store them in an more static but much more efficient format
		kbase = new CSC2Dfloat(link_strengths);

		// kbase = new SparseFloatMatrix2D(src_map.size(), targ_map.size());
		// cooccurrence_counts.forEachNonZero(new IntIntIntFunction() {
		//
		// @Override
		// public int apply(int row, int col, int value) {
		// kbase.setQuick(row, col, link_strength(value
		// / (float) row_count[col]));
		// return value;
		// }
		// });
	}

	/**
	 * return the percentage of co-occurrences less than the given threshold in
	 * the co-occurrence count matrix
	 * 
	 * @param threshold
	 * @return number of non zero element less than the threshold value divided
	 *         by total of non zero element
	 */
	public float get_percent_element_less_threshold(int threshold)
			throws NullPointerException {

		int count = 0;
		for (Entry<Pair<Integer, Integer>, Integer> e : cooccurrence_counts
				.nz_elements()) {
			if (e.getValue() < threshold) {
				count += 1;
			}
		}
		return count / cooccurrence_counts.nnz();
	}

	/**
	 * transmit the excitation of Module <em>mod</em> over this knowledge base
	 * 
	 * @param normalized_excitations
	 *            non-null, sum of all elements == 1
	 * @return the excitations resulting from the transmission of <em>mod</em>'s
	 *         excitation
	 * @throws IllegalArgumentException
	 *             if the size of the normalized_excitations is not correct
	 * @throws NullPointerException
	 *             if the link strengths were not recomputed since the last
	 *             count modification
	 */
	public DOK1Dfloat transmit(DOK1Dfloat normalized_excitations)
			throws IllegalArgumentException, NullPointerException {

		if (normalized_excitations.nlines() != src_map.size()) {
			throw new IllegalArgumentException(
					"Input excitations should match the size of the input wordsmapping");
		}

		return kbase.multiply(normalized_excitations);
	}

	/**
	 * computes the excitation function ln(p / pzero) + B
	 * 
	 * @param antecedent_support_probability
	 * @return the value of the excitation ln(p / pzero) + B, or 0 if the
	 *         antecedent_support_probability <= pzero
	 * @see ConfConstant
	 * @see /report/february_symbol_level_confabulation_theory/
	 */
	public static float link_strength(double antecedent_support_probability) {
		if (antecedent_support_probability > ConfConstant.pzero) {
			return (float) Math.log(antecedent_support_probability
					/ (double) ConfConstant.pzero)
					+ ConfConstant.B;
		}
		return 0;
	}

	public String stats() {
		return "number of knowledge links: " + n_knowledge_links();
	}

	/**
	 * @return the number of symbols of the source module
	 */
	public int size_src() {
		return cooccurrence_counts.ncols();
	}

	/**
	 * @return the number of symbols of the target module
	 */
	public int size_targ() {
		return cooccurrence_counts.nlines();
	}

	/**
	 * @return the number of knowledge links of this kbase
	 * @throws NullPointerException
	 *             is the links were not recomputed since last modification of
	 *             the counts
	 * @see #compute_link_strengths()
	 */
	public int n_knowledge_links() throws NullPointerException {
		return kbase.nnz();
	}
}
