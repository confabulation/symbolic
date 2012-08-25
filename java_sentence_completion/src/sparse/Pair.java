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
public class Pair<A, B> implements Entry<A, B> {
	public final A first;
	public final B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	public boolean equals(Object other) {
		if (other instanceof Pair<?, ?>) {
			Pair<?, ?> otherPair = (Pair<?, ?>) other;
			return ((this.first == otherPair.first || (this.first != null
					&& otherPair.first != null && this.first
						.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
					&& otherPair.second != null && this.second
						.equals(otherPair.second))));
		}

		return false;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public A getKey() {
		return first;
	}

	@Override
	public B getValue() {
		return second;
	}

	/**
	 * Immutable
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public B setValue(B value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Immutable");
	}

}
