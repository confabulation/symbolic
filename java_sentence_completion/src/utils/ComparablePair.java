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
package utils;

import sparse.Pair;

/**
 * @author bernard and cedric
 * 
 */
public class ComparablePair<A extends Comparable<A>, B extends Comparable<B>>
		extends Pair<A, B> implements Comparable<ComparablePair<A, B>>{


	public ComparablePair(A first, B second) {
		super(first, second);
	}

	@Override
	public int compareTo(ComparablePair<A, B> o) {
		int a = first.compareTo(o.first);
		if (a != 0){
			return a;
		}
		return second.compareTo(o.second);
	}

}
