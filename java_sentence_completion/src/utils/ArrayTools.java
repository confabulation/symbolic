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

package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * some utilities
 * 
 * @author bernard paulus and cedric snauwaert
 */
public class ArrayTools {

	/**
	 * checks whether element is in the sub list starting at <em>from</em>
	 * inclusive to <em>to</em> exclusive
	 * 
	 * @return True if the element is in the sublist
	 */
	public static <T> boolean in_list(T elem, T[] list, int from, int to) {
		int end = Math.min(list.length, to);
		if (elem == null) {
			for (int i = from; i < end; i++) {
				if (list[i] == null) {
					return true;
				}
			}
		} else {
			for (int i = from; i < end; i++) {
				if (elem.equals(list[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if elem is in list
	 * 
	 * @param elem
	 * @param list
	 *            can be null
	 * @return true if elem is in list, false otherwise
	 */
	public static boolean in(boolean elem, boolean... list) {
		if (list == null) {
			return false;
		}
		for (boolean e : list) {
			if (elem == e) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if elem is in list
	 * 
	 * @param elem
	 * @param list
	 *            can be null
	 * @return true if elem is in list, false otherwise
	 */
	public static boolean in(int elem, int... list) {
		if (list == null) {
			return false;
		}
		for (int i : list) {
			if (elem == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * python's 'in' operator
	 * 
	 * <br/>
	 * <br/>
	 * Tip: <code>import static confabulation.Utils.in;</code>
	 * 
	 * @return true if elem in list, false otherwise
	 */
	public static <T> boolean in(T elem, T... list) {
		if (list == null) {
			return false;
		}
		if (elem == null) {
			for (T e : list) {
				if (e == null) {
					return true;
				}
			}
		} else {
			for (T e : list) {
				if (elem.equals(e)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove every occurrence of elem in array
	 * 
	 * @param array
	 *            not null
	 * @return a new array with every occurence of elem removed
	 */
	public static <T> T[] removeEvery(final T elem, final T[] array) {
		T[] tmp = Arrays.copyOf(array, array.length); // new T[array.length]
		int n = 0; // does not work
		if (elem == null) {
			for (T e : array) {
				if (e != null) {
					tmp[n] = e;
					n++;
				}
			}
		} else {
			for (T e : array) {
				if (!elem.equals(e)) {
					tmp[n] = e;
					n++;
				}
			}
		}
		return Arrays.copyOf(tmp, n);
	}

	/**
	 * indexes of each element equal to elem
	 * 
	 * @param array
	 *            if null, assume the empty list
	 * @return list of indexes of elements from array equal to elem, in
	 *         increasing order
	 */
	public static <T> int[] find_equals(T elem, T... array) {
		if (array == null) {
			return new int[0];
		}
		int[] tmp = new int[array.length];
		int n = 0;
		if (elem == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					tmp[n] = i;
					n++;
				}
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				if (elem.equals(array[i])) {
					tmp[n] = i;
					n++;
				}
			}
		}
		return Arrays.copyOf(tmp, n);
	}
	
	/**
	 * indexes of each element not equal to elem
	 * 
	 * @param array
	 *            if null, assume the empty list
	 * @return list of indexes of elements from array not equal to elem, in
	 *         increasing order
	 */
	public static <T> int[] find_not_equals(T elem, T... array) {
		if (array == null) {
			return new int[0];
		}
		int[] tmp = new int[array.length];
		int n = 0;
		if (elem == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					tmp[n] = i;
					n++;
				}
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				if (!elem.equals(array[i])) {
					tmp[n] = i;
					n++;
				}
			}
		}
		return Arrays.copyOf(tmp, n);
	}

	/**
	 * Get all the values corresponding to indexes equal to key.
	 * <p>
	 * O(n) search
	 * </p>
	 * <p>
	 * TODO second method in O(log(n)), on sorted arrays
	 * </p>
	 * 
	 * @param key
	 * @param keys
	 *            non-null, keys.length == values.length
	 * @param values
	 *            non-null, keys.length == values.length
	 * @return
	 */
	public static <K, V> V[] get_map(K key, K[] keys, V[] values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException("keys.length (" + keys.length
					+ ") != values.length (" + values.length + ")");
		}
		int[] indexes = find_equals(key, keys);

		// TODO extract copy_indexes() method that copies only given indexes
		V[] ret = Arrays.copyOf(values, indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			ret[i] = values[indexes[i]];
		}
		return ret;
	}

	/**
	 * indexes of each element equal to elem
	 * 
	 * @param array
	 *            if null, assume the empty list
	 * @return list of indexes of elements from array equal to elem, in
	 *         increasing order
	 */
	public static int[] find_equals(boolean elem, boolean... array) {
		if (array == null) {
			return new int[0];
		}
		int[] tmp = new int[array.length];
		int n = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == elem) {
				tmp[n] = i;
				n++;
			}
		}
		return Arrays.copyOf(tmp, n);
	}

	/**
	 * counts the number of elements equal to elem
	 * 
	 * @return
	 */
	public static <T> int number_equal(T elem, T... array) {
		if (array == null) {
			return 0;
		}
		int c = 0;
		if (elem == null) {
			for (T t : array) {
				if (t == null) {
					c++;
				}
			}
		} else {
			for (T t : array) {
				if (elem.equals(t)) {
					c++;
				}
			}
		}
		return c;
	}
	
	/**
	 * @param elem
	 * @param array
	 * @return the number of elements of array not equal to elem
	 */
	public static <T> int number_not_equal(T elem, T... array) {
		if (array == null) {
			return 0;
		}
		return array.length - number_equal(elem, array);
	}

	/**
	 * find the first element equal to elem in ts
	 * 
	 * @return it's index, or -1 if not found
	 */
	public static <T> int find_equal(T elem, T... ts) {
		if (ts == null) {
			return -1;
		}
		if (elem == null) {
			for (int i = 0; i < ts.length; i++) {
				if (ts[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < ts.length; i++) {
				if (elem.equals(ts[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * find the first element not equal to elem in array
	 * 
	 * @return it's index, or -1 if not found
	 */
	public static <T> int find_not_equal(T elem, T... array) {
		if (array == null) {
			return -1;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] != elem) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * the intersection of the two lists
	 * <p>
	 * time complexity: O(n^2)
	 * </p>
	 * <p>
	 * BEWARE: duplicates in the shortest link will be kept!!!
	 * 
	 * </p>
	 * 
	 * @param array1
	 *            non-null (beware of duplicates)
	 * @param array2
	 *            non-null (beware of duplicates)
	 * @return the elements that are in array1 and in array2, as defined by
	 *         {@link T#equals(Object)}, in the same order
	 * @throws NullPointerException
	 *             if an array is null
	 */
	public static <T> T[] intersect(T[] array1, T[] array2)
			throws NullPointerException {
		// TODO fix intersect for duplicates
		int n = 0;

		T[] ret;
		if (array1.length > array2.length) {
			ret = Arrays.copyOf(array2, array2.length);
		} else {
			ret = Arrays.copyOf(array1, array1.length);
		}

		// code ok because elem is at an index superior or higher to ret[n]
		// note that this would probably throw if used with collection
		for (T elem : ret) {
			if (in(elem, array1)) {
				ret[n] = elem;
				n++;
			}
		}

		return Arrays.copyOf(ret, n);
	}

	/**
	 * enumerate all sublists of length <em>l</em>
	 * 
	 * @param l
	 *            the length of the sublists
	 * @param objs
	 * @return the list of all sublists
	 */
	public static Object[][] enumerate_sublists(int l, Object... objs) {
		Vector<Object[]> ret = new Vector<Object[]>();
		Vector<Object> in = new Vector<Object>(Arrays.asList(objs));

		for (int i = 0; i <= in.size() - l; i++) {
			ret.add(in.subList(i, i + l).toArray(new Object[0]));
		}
		return ret.toArray(new Object[0][0]);
	}

	/**
	 * Collate the representation of each of the elements <em>ts</em> with
	 * <em>conjunction</em> between
	 * 
	 * @param conjunction
	 * @param ts
	 * @return the collated string
	 */
	public static <T> String join(final String conjunction, T... ts) {
		String ret = "";
		for (int i = 0; i < ts.length - 1; i++) {
			ret += ts[i] + conjunction;
		}
		return ret + ts[ts.length - 1];
	}

	/**
	 * Returns all the possibilities to fill a T[] vector, given ts, the array
	 * of possibilities for each position
	 * 
	 * @param ts
	 *            the array of possibilities for each position of the array
	 * @return <p>
	 *         if ts == null or ts.length == 0, return null
	 *         </p>
	 *         <P>
	 *         otherwise, return ret, such that ret.length == Product_k
	 *         ts[k].length
	 *         </p>
	 *         <p>
	 *         and ret[i].length == ts.length pour tout i
	 *         </p>
	 */
	public static <T> T[][] enumerate_all_possibilities(final T[]... ts) {
		if (ts == null || ts.length == 0) {
			return null;
		}
		return enumerate_all_possibilities(Arrays.copyOf(ts[0], 0), ts)
				.toArray(Arrays.copyOf(ts, 0));
	}

	/**
	 * recursive helper function to {@link
	 * #enumerate_all_possibilities(Object[]...)}
	 */
	protected static <T> List<T[]> enumerate_all_possibilities(T[] begin,
			T[][] remaining) {
		LinkedList<T[]> ret = new LinkedList<T[]>();
		if (remaining.length == 0) {
			ret.add(begin);
			return ret;
		}
		for (T e : remaining[0]) {
			T[] new_begin = Arrays.copyOf(begin, begin.length + 1);
			new_begin[begin.length] = e;
			ret.addAll(enumerate_all_possibilities(new_begin,
					Arrays.copyOfRange(remaining, 1, remaining.length)));
		}
		return ret;
	}

	/**
	 * Replace each occurrence of <em>elem</em> by it's corresponding element in
	 * <em>by</em> in array <em>in</em>
	 * 
	 * @param elems
	 *            the list of elems to replace
	 * @param by
	 *            the list of by what to replace the elems.
	 *            <p>
	 *            Need: by.length == elems.lengths
	 *            </p>
	 *            If two elems are equal, remplace only by the last one.
	 * @param in
	 *            the list where to replace each element of elems by it's
	 *            corresponding element in by
	 */
	public static <T> T[] replace_each(T[] elems, T[] by, T[] in) {
		T[] ret = Arrays.copyOf(in, in.length);

		HashMap<T, T> map = new HashMap<T, T>();
		for (int i = 0; i < elems.length; i++) {
			map.put(elems[i], by[i]);
		}

		for (int i = 0; i < ret.length; i++) {
			if (map.containsKey(in[i])) {
				ret[i] = map.get(in[i]);
			}
		}
		return ret;
	}

	/**
	 * replace each occurrence of each element of elems by <em>by</em>
	 * <p>
	 * Programmer note: this should be named replace_each(), but due to type
	 * erasure, java mixes the two methods when T is an array, String[] for
	 * instance.
	 * </p>
	 * 
	 * @see #replace_each(Object[], Object[], Object[])
	 */
	public static <T> T[] replace_all_by(T[] elems, T by, T[] in) {
		T[] bys = Arrays.copyOf(elems, elems.length);
		Arrays.fill(bys, by);
		return replace_each(elems, bys, in);
	}

	/**
	 * returns a list where corresponding elements are put toghether.
	 * <p>
	 * Example:</br> zip(new Integer[]{1, 2, 3}, new Integer[]{4, 5, 6})</br>
	 * returns</br> [[1, 4], [2, 5], [3, 6]]
	 * </p>
	 * 
	 * @param ts
	 *            the lists to zip, with each length equal to the length of the
	 *            first. Still works if lengths of the other lists are greater
	 *            that the length of the first, but the extra elements will be
	 *            ignored
	 * @return the zipped list
	 */
	public static <T> T[][] zip(T[]... ts) {
		T[][] ret = Arrays.copyOf(ts, ts[0].length);
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOf(ts[0], ts.length);
			for (int j = 0; j < ts.length; j++) {
				ret[i][j] = ts[j][i];
			}
		}
		return ret;
	}

	/**
	 * create an array and fills it with the given elements.
	 * <ul>
	 * <li>
	 * If size is over the number of elements, the array is separated in equal
	 * sections and each section is filled with the corresponding element.</li>
	 * <li>
	 * if size over the number of elements, and the array cannot be separated in
	 * equal sections, the first sections will be a single element longer than
	 * the last one, until the remaining array can be split equally</li>
	 * <li>
	 * If size is less than the number of elements, those are truncated.</li>
	 * </ul>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * new_fill(6, 1, 2); </br> -> [ 1, 1, 1, 2, 2, 2 ]
	 * </p>
	 * <p>
	 * new_fill(6, 1, 2, 3, 4); </br> -> [ 1, 1, 2, 2, 3, 4 ]
	 * </p>
	 * <p>
	 * new_fill(3, 1, 2, 3, 4, 5); </br> -> [ 1, 2, 3 ]
	 * </p>
	 * 
	 * @param size
	 *            >= 0
	 * @param elems
	 * @return <ul>
	 *         <li>the new array of length <em>size</em>, filled with the elems</li>
	 *         <li>null if elems is null</li>
	 *         <li>the new array filled with null if elems has no elements</li>
	 *         </ul>
	 */
	public static <T> T[] new_fill(final int size, T... elems) {
		if (elems == null) {
			return null;
		}
		T[] ret = Arrays.copyOf(elems, size);
		if (elems.length == 0) {
			return ret;
		}

		int i = 0;
		int j = 0;
		final int chunk_size = size / elems.length;

		// fill first sections with one additional element
		while ((size - i) - chunk_size * (elems.length - j) != 0) {
			Arrays.fill(ret, i, i + chunk_size + 1, elems[j]);
			i += chunk_size + 1;
			j++;
		}

		// fill remaining of array with sections of equal length
		for (; i < size; i += chunk_size, j++) {
			Arrays.fill(ret, i, i + chunk_size, elems[j]);
		}
		return ret;
	}

	/**
	 * Concatenates multiple arrays together
	 * 
	 * @param tss
	 *            the arrays to concatenate, must contain at least a single
	 *            non-null element
	 * @return a new array containing a shallow copy of the contents of the
	 *         given arrays, in the order they where given, or null if tss was
	 *         null
	 */
	public static <T> T[] concat(T[]... tss) {

		int size = 0;
		T[] non_null = null;
		for (T[] ts : tss) {
			if (ts != null) {
				size += ts.length;
				non_null = ts;
			}
		}

		T[] ret = Arrays.copyOf(non_null, size);
		int index = 0;
		for (T[] ts : tss) {
			if (ts != null) {
				System.arraycopy(ts, 0, ret, index, ts.length);
				index += ts.length;
			}
		}
		return ret;
	}

	/**
	 * Copy the array while keeping the elements at <em>indexes</em> and filling
	 * the rest of the <em>elements</em> by fill
	 * 
	 * @param fill
	 *            element to use as filling. can be null.
	 * @param indexes
	 *            non-null, each index must be in [0, <em>ts</em>.length[
	 * @param ts
	 *            non-null, the array that will be partially copied
	 * @return a new array, ret, of the same length as <em>ts</em>.
	 *         <ul>
	 *         <li>ret[i] == <em>ts</em>[i] if i is in <em>indexes</em></li>
	 *         <li>ret[i] == <em>fill</em> if i is not in <em>indexes<em></li>
	 *         </ul>
	 */
	public static <T> T[] copy_indexes_fill_others(T fill, int[] indexes, T[] ts) {
		T[] ret = Arrays.copyOf(ts, ts.length);
		Arrays.fill(ret, fill);
		for (int i : indexes) {
			ret[i] = ts[i];
		}
		return ret;

	}

	/**
	 * check if each element is equal to the first
	 * 
	 * @param elem
	 * @param is
	 *            non-null
	 * @return true if all the elements are equals (since == is transitive),
	 *         false otherwise
	 */
	public static boolean all_equals(int elem, int... is) {
		for (int i : is) {
			if (i != elem) {
				return false;
			}
		}
		return true;
	}

	/**
	 * count from 0 inclusive to stop exclusive
	 * 
	 * @param start
	 * @param stop
	 * @return range(0, stop, 1);
	 * @see #range(int, int, int)
	 */
	public static int[] range(int stop) {
		return range(0, stop, 1);
	}

	/**
	 * count from start inclusive to stop exclusive
	 * 
	 * @param start
	 * @param stop
	 * @return range(start, stop, 1);
	 * @see #range(int, int, int)
	 */
	public static int[] range(int start, int stop) {
		return range(start, stop, 1);
	}

	/**
	 * build the list created by summing step to start until finding stop. Stop
	 * is never included in the list
	 * 
	 * @param start
	 * @param stop
	 * @param step
	 * @return the list created by summing step to start until finding stop,
	 *         stop non included. If step == 0 or if step does not allow the
	 *         algorithm to terminate, return the empty list.
	 *         <p>
	 *         ex:
	 *         <ul>
	 *         <li>range(1, 4, 1) -> [1, 2, 3]</li>
	 *         <li>range(-10, -4, 2) -> [-10, -8, -6]</li>
	 *         <li>range(1, 4, -1) -> []</li>
	 *         </ul>
	 *         </p>
	 * @see #range(int)
	 * @see #range(int, int)
	 */
	public static int[] range(int start, int stop, int step) {
		if (step == 0) {
			return new int[0];
		}

		int n;
		if (step > 0) {
			n = (stop - start + (step - 1)) / step;
		} else {
			n = (stop - start + (step + 1)) / step;
		}

		if (n < 0) {
			return new int[0];
		}

		int[] ret = new int[n];
		for (int i = 0; i < n; i++) {
			ret[i] = start;
			start += step;
		}
		return ret;
	}
}
