/**
 * 
 */
package utils;

/**
 * @author bernard
 * 
 */
public class MathTools {

	/**
	 * Ramp function
	 * 
	 * <a href="https://en.wikipedia.org/wiki/Ramp_function">wikipedia<a>
	 * 
	 * @param x
	 * @return x if x > 0; 0 otherwise
	 */
	public static int ramp_fun(int x) {
		return x > 0 ? x : 0;
	}

	/**
	 * Ramp function
	 * 
	 * <a href="https://en.wikipedia.org/wiki/Ramp_function">wikipedia<a>
	 * 
	 * @param x
	 * @return x if x > 0; 0 otherwise
	 */
	public static float ramp_fun(float x) {
		return x > 0 ? x : 0;
	}

	/**
	 * sum over the arguments
	 * 
	 * @param ints
	 *            non-null
	 * @return the sum of each element of ints
	 */
	public static int sum(int... ints) {
		int sum = 0;
		for (int i : ints) {
			sum += i;
		}
		return sum;
	}
	
	/**
	 * minimum
	 * @param is non-null, length > 0
	 * @return the minimal element
	 */
	public static int min(int... is) {
		int min = is[0];
		for (int i : is) {
			if (i < min){
				min = i;
			}
		}
		return min ;
	}
	
	/**
	 * maximum
	 * @param is non-null, length > 0
	 * @return the maximal element
	 */
	public static int max(int... is) {
		int max = is[0];
		for (int i : is) {
			if (i > max){
				max = i;
			}
		}
		return max ;
	}
}
