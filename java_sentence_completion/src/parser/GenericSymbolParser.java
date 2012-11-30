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

package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import utils.RuntimeIOException;

import static utils.ArrayTools.in;

/**
 * A parametric parser.
 * <p>
 * The essential parameter is the {@link Tokenizer} which controls the parsing
 * process.
 * </p>
 * 
 * @author bernard and Cédric
 * 
 */
public class GenericSymbolParser {

	protected FileInputStream fis;
	// private BufferedInputStream bis;
	// private DataInputStream stream;
	protected BufferedReader stream;

	String file;
	protected Tokenizer tok;
	/**
	 * whether the parser is at the beginning of the file
	 */
	protected boolean head_of_file;

	/**
	 * Open the file for reading. Parses the line word by word
	 * 
	 * @param file
	 *            non-null, a readable file
	 */
	public GenericSymbolParser(String file) throws IOException {
		this(file, new WordTokenizer());
	}

	/**
	 * Open the file for reading
	 * 
	 * @param file
	 *            non-null, a readable file
	 * @param tok
	 *            non-null the tokenizer
	 * @throws IOException
	 */
	public GenericSymbolParser(String file, Tokenizer tok) throws IOException {
		if (in(null, file, tok)) {
			throw new NullPointerException("file (" + file + "), or mtok ("
					+ tok + ") is null");
		}
		this.file = file;
		if (!new File(file).canRead()) {
			throw new FileNotFoundException(file + " doesn't exists");
		}
		this.tok = tok;
		fis = null;
		stream = null;
		head_of_file = true;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			// bis = new BufferedInputStream(fis);
			// stream = new DataInputStream(bis);

			stream = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes and reopens file, without relinquishing all the file descriptors
	 * 
	 * @throws RuntimeIOException
	 *             when an {@link IOException} occurs
	 */
	public void restart() throws RuntimeIOException {
		try {
			fis = new FileInputStream(file);
			stream.close(); // closes the old fd
			stream = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		head_of_file = true;
	}

	/**
	 * Only {@link #restart()} if the parser has been used to read from the file
	 */
	public void restart_if_used() {
		if (!head_of_file) {
			restart();
		}
	}

	/**
	 * get the symbols from a line of the file
	 * 
	 * @return null at the end of the file, or a an array of symbols. null
	 *         elements in that array indicate words that do not result in a
	 *         symbol
	 * @see #restart()
	 */
	public String[] getLine() throws RuntimeIOException {

		head_of_file = false; // mark we tried to read

		String line;
		try {
			line = stream.readLine();
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		return tok.parse(line, "( |\t)");
	}

	/**
	 * Closes the file
	 * 
	 * @throws RuntimeIOException
	 */
	public void close() throws RuntimeIOException {
		try {
			stream.close(); // ok even when called twice
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

	}
}
