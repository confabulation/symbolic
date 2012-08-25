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

package sparse;

import java.util.Map.Entry;

/**
 * HashMap compatible C++-like Pair
 * 
 * @author arturh@stackoverflow.com + edits by bernard
 * 
 * @param <A>
 * @param <B>
 */
public class PairOfInts implements Comparable<PairOfInts> {
	public final int first;
	public final int second;

	public PairOfInts(int first, int second) {
		this.first = first;
		this.second = second;
	}
	
	public PairOfInts(Entry<Integer, Integer> entry){
		first = entry.getKey();
		second  = entry.getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		PairOfInts other = (PairOfInts) obj;
		return first == other.first && second == other.second;
	}

	@Override
	public int compareTo(PairOfInts o) {
		int first_diff = first - o.first;
		if (first_diff != 0) {
			return first_diff;
		}
		return second - o.second;
	}

	/**
	 * put the second number in first position, and the first in second
	 * <p>
	 * Matthew 20:16 : "So the last will be first, and the first will be last."
	 * </p>
	 * 
	 * @return a new object with the inverted fields
	 */
	public PairOfInts reverse() {
		return new PairOfInts(second, first);
	}
}
