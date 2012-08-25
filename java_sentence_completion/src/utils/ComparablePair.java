/**
 * 
 */
package utils;

import sparse.Pair;

/**
 * @author bernard
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
