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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Generic Trie implementation based on hashtables
 * 
 * @author bernard
 * 
 */
public class HashStandardTrie<E> {

	protected Node root;
	protected int size;

	public HashStandardTrie() {
		root = new Node(null, false); // base node with no value
		size = 0;
	}

	public HashStandardTrie(Iterable<Iterable<E>> sequences) {
		this();
		add_all(sequences);
	}

	/**
	 * returns a list of all the matches found, sorted in increasing length
	 * 
	 * @param sequence
	 *            non-null
	 * @return the list of all matches. Empty if no match is found
	 */
	public List<List<E>> find_all(Iterable<E> sequence) {
		List<List<E>> ret = new LinkedList<List<E>>();
		LinkedList<E> trace = new LinkedList<E>();
		Node cur = root;
		for (E e : sequence) {

			Node child = cur.get(e);
			if (child == null) {
				return ret;
			}
			trace.add(e);

			if (child.is_final()) {
				ret.add(new LinkedList<E>(trace));
			}
			cur = child;
		}
		return ret;
	}

	/**
	 * find the longest match of the sequence. The longest match is a prefix of
	 * the given sequence: this does not return possible matches beyond the
	 * length of the sequence.
	 * 
	 * @param sequence
	 *            non-null
	 * @return the longest match, or an empty list of there is none
	 */
	public List<E> find_longest(Iterable<E> sequence) {
		LinkedList<E> ret = new LinkedList<E>();
		LinkedList<E> unconfirmed = new LinkedList<E>();
		Node cur = root;
		for (E e : sequence) {

			Node child = cur.get(e);
			if (child == null) {
				return ret;
			}

			unconfirmed.add(e);
			if (child.is_final()) {
				ret.addAll(unconfirmed);
				unconfirmed = new LinkedList<E>();
			}
			cur = child;
		}
		return ret;
	}

	/**
	 * add the sequence in the trie
	 * 
	 * @param sequence
	 *            null is ignored. This method has no effect if sequence is
	 *            already in the trie
	 */
	public void add(Iterable<E> sequence) {
		if (sequence == null) {
			return;
		}
		Node cur = root;
		for (E e : sequence) {
			cur = cur.put(e, false);
		}
		if (!cur.is_final()) {
			cur.set_final(true);
			size++;
		}
	}

	/**
	 * insert all the present sequences in the three
	 * 
	 * @param sequences
	 *            null is ignored
	 */
	public void add_all(Iterable<Iterable<E>> sequences) {
		if (sequences == null) {
			return;
		}
		for (Iterable<E> seq : sequences) {
			add(seq);
		}
	}

	/**
	 * number of sequences stored in the trie
	 */
	public int size() {
		return size;
	}

	/**
	 * Node for the Trie
	 * 
	 * @author bernard
	 * 
	 */
	protected class Node {

		protected HashMap<E, Node> map;
		protected E v;
		protected boolean final_n;

		public Node(E value, boolean final_node) {
			map = new HashMap<E, Node>();
			v = value;
			final_n = final_node;
		}

		/**
		 * If this is a possible end, even when non-leaf
		 * 
		 * @return
		 */
		public boolean is_final() {
			return final_n;
		}

		public void set_final(boolean final_node) {
			final_n = final_node;
		}

		public boolean is_leaf() {
			return map.size() == 0;
		}

		/**
		 * Create a child node if node does not exist yet
		 * 
		 * @param child
		 * @param final_node
		 *            set the child node as final if set. Otherwise, don't
		 *            change (boolean OR)
		 * @return the newly created node, or the already existing one, if there
		 *         was one
		 */
		public Node put(E child, boolean final_node) {
			Node c_node = map.get(child);
			if (c_node != null) {
				if (final_node) {
					c_node.set_final(final_node);
				}
				return c_node;
			}
			Node new_child = new Node(child, final_node);
			map.put(child, new_child);
			return new_child;
		}

		public Node get(E child) {
			return map.get(child);
		}
	}
}
