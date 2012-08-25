package confabulation;

/**
 * This class contains the different constants for a confabulation experiment
 * 
 * @author cedric
 */
public class ConfConstant {

	/**
	 * minimal probability of co-occurrence considered in the confabulation
	 */
	public static final float pzero = 0.0001F;
	/**
	 * Bandwidth term (see Confabulation Theory: the mechanism of thought p.140)
	 */
	public static final int B = 100;
	
	/**
	 * state wheter statistic component must be compute and printed (like percentage of element less
	 * then threshold in knowledge base, ...)
	 */
	private boolean statistic=false;
	
	/**
	 * Maximal number of knowledge link inputs <br/>
	 * <br/>
	 * Computed according to the excitation function: <br/>
	 * $I(\lambda) = \sum_{i = 1}^{N} [ln(p(\alpha_i | \lambda) / pzero) + B]$ <br/>
	 * The values will all be between NB and N(ln(1/pzero) + B) and those
	 * shouldn't interfere: <br/>
	 * (N+1)B > NB + N ln(1/pzero). <br/>
	 * B > N ln(1/pzero) <br/>
	 * This gives: <br/>
	 * N < B/ ln(1/pzero) <br/>
	 * Double.MIN_VALUE is substracted to ensure Nmax is strictly lower
     * <p>
     * !!! in fact N + 1 is ok too because no higher band is supposed to be used
     * TODO modify Nmax to N + 1
     * </p>
	 */
	public static final int Nmax = (int) (B / Math.log(1.0 / pzero) - Double.MIN_VALUE);

	int Nbmodules;

	public ConfConstant() {
		Nbmodules = 0;
	}

	public void setNumberModule(int number) {
		/*if (number > Nmax) {
			throw new IllegalArgumentException(
					"trying a confabulation with tood much modules:\n"
							+ "Nmax=" + Nmax + "\ngiven argument=" + number);
		}*/
		Nbmodules = number;
	}

	public int getNumberModule() {
		return Nbmodules;
	}
	
	public boolean getStatistic(){
		return statistic;
	}
	
	public void setStatistic(boolean stat){
		this.statistic=stat;
	}

}
