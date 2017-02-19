/**
 * Copyright 2011-2012 Bernard Paulus and Cédric Snauwaert
 * 
 * Confabulation_Symbolic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Confabulation_Symbolic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Confabulation_Symbolic.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package com.github.confabulation.symbolic.utils;

/**
 * @author bernard and cedric
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
