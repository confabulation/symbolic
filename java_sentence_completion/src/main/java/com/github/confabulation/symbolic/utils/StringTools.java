package com.github.confabulation.symbolic.utils;

public class StringTools {

	/**
	 * Fold the line up to <em>max_chars</em> characters + the line feed,
	 * splitting on spaces when possible
	 * 
	 * @param max_chars
	 *            > 0
	 * @param s
	 *            non null
	 * @return the folded line
	 */
	public static String fold(int max_chars, String s) {
		String ret = "";
		int last_split = 0;
		int last_space = -1;

		for (int i = 0; i < s.length(); i++) {

			if (s.charAt(i) == ' ' || s.charAt(i) == '\n') {
				last_space = i;
			}

			if (i - last_split == max_chars) {
				if (last_space < last_split) { // no space in the line.
												// Split the long word
					ret += s.substring(last_split, i) + '\n';
					last_split = i;
				} else {
					ret += s.substring(last_split, last_space) + '\n';
					i = last_space; // go back just after the space
					last_split = i + 1;
				}
			}
		}
		if (last_split != s.length()) {
			ret += s.substring(last_split);
		}
		return ret;
	}
}
