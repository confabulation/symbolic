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
package com.github.confabulation.symbolic.colt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cern.colt.list.tfloat.FloatArrayList;
import cern.colt.list.tint.IntArrayList;
import cern.colt.matrix.tfloat.impl.SparseFloatMatrix2D;

/**
 * Class for matrix IO
 * 
 * @author bernard and cedric
 * 
 */
public class SparseFloatMatrix2DSaver {

	private static final String marker = "SparseFloatMatrix2D";

	/**
	 * Write a matrix to a file
	 * 
	 * @param m
	 *            can't be null
	 * @param filename
	 *            must be a valid path string
	 * @post if the file exists, a backup is created before overwriting the file
	 *       is written in the format described in write(SparseFloatMatrix,
	 *       Writer)
	 * 
	 */
	public static void write(SparseFloatMatrix2D m, String filename) {
		File f = new File(filename);
		if (f.exists()) {
			f.renameTo(new File(f.getPath() + " backup " + new Date()));
		}
		try {
			Writer out = new FileWriter(filename);
			write(m, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * writes a matrix to an opened writer
	 * 
	 * @param m
	 *            can't be null
	 * @param w
	 *            must be open
	 * @throws IOException
	 * @post if everything goes fine, the matrix is written in the following
	 *       format: Header line, as "SparseFloatMatrix2D (" + m.rows() + "," +
	 *       m.columns() + ")" The following lines, for the elements, in CSV
	 *       format: row_index,column_index,value The last line is
	 *       "SparseFloatMatrix2D END"
	 */
	public static void write(SparseFloatMatrix2D m, Writer w)
			throws IOException {
		IntArrayList rows = new IntArrayList();
		IntArrayList cols = new IntArrayList();
		FloatArrayList values = new FloatArrayList();
		m.getNonZeros(rows, cols, values);
		BufferedWriter out = new BufferedWriter(w);
		out.write(marker + " (" + m.rows() + "," + m.columns() + ")");
		out.newLine();
		for (int i = 0; i < rows.size(); i++) {
			out.write(rows.get(i) + "," + cols.get(i) + "," + values.get(i));
			out.newLine();
		}
		out.write(marker + " END");
		out.newLine();
		out.flush();
	}

	/**
	 * Reads a matrix from a file, in the format written by write()
	 * 
	 * @param filename
	 *            name of an existing and correctly formatted file
	 * @return a new matrix containing the values of the file
	 * @throws FileNotFoundException
	 *             if the file was not found
	 * @throws FormatErrorException
	 *             if any format error is detected
	 */
	public static SparseFloatMatrix2D read(String filename)
			throws FileNotFoundException, FormatErrorException {
		Reader in = new FileReader(filename);
		try {
			SparseFloatMatrix2D ret = read(in);
			in.close();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads a matrix from a reader, in the format written by write()
	 * 
	 * @param r non-null open reader
	 * @return a new matrix containing the values read
	 * @throws FormatErrorException if any format error is detected
	 * @throws IOException if some unexpected read error occurs
	 */
	public static SparseFloatMatrix2D read(Reader r)
			throws FormatErrorException, IOException {
		BufferedReader in = new BufferedReader(r);
		try {
			String header = in.readLine();
			if (header == null) {
				throw new FormatErrorException("file seems empty");
			}
			Pattern pat = Pattern.compile(marker + " \\((.+),(.+)\\)");
			Matcher matcher = pat.matcher(header);
			if (!matcher.find()) {
				throw new FormatErrorException(marker + " header incorrect");
			}
			int nrows = Integer.parseInt(matcher.group(1));
			int ncolumns = Integer.parseInt(matcher.group(2));
			SparseFloatMatrix2D ret = new SparseFloatMatrix2D(nrows, ncolumns);

			String csv_data = null;
			while ((csv_data = in.readLine()) != null) {
				if (csv_data.equals(marker + " END")) {
					break;
				}
				Scanner scan = new Scanner(csv_data).useDelimiter(",");
				int row = scan.nextInt();
				int column = scan.nextInt();
				double value = scan.nextDouble();
				ret.set(row, column, (float) value);
			}
			// no close() here: allow user to append more data
			return ret;
		} catch (NumberFormatException e){
			throw new FormatErrorException("failed to parse the size of the matrix");
		} catch (InputMismatchException e) {
			throw new FormatErrorException(
					"found some wrong data in supposed csv body\n"
							+ "original exception" + e);
		} catch (NoSuchElementException e) {
			throw new FormatErrorException(
					"found some wrong data in supposed csv body\n"
							+ "original exception" + e);
		}
	}
	
	/**
	 * parse a header like "'header_prefix' (param1,param2,...)" from a line
	 * 
	 * @param header_prefix a regexp. If null, header of form "(...)" is assumed
	 * @param input non-null string, supposedly of form "'header_prefix' (...)"
	 * 			... the three dots are separated comma values, with commas separating the fields.
	 * 			Their special meaning can be escaped by a backslash.
	 * 			Because of this, backslashes in front of a comma should be escaped if meant to be part of the input
	 * @return the list of parameter strings that are separated by comma, or null if nothing is found
	 */
	public static String[] parse_header(String header_prefix, String input){
		Matcher head = Pattern.compile(header_prefix + "\\s*\\((.*)\\)").matcher(input);
		if (! head.find()){
			return null;
		}
		String csv_data = head.group();
		Matcher csv = Pattern.compile(",|(.*?)(?:,|$)").matcher(csv_data);
		Queue<String> ret = new LinkedList<String>();
		String p = "";
		while(csv.find()){
			p += csv.group(1) != null ? csv.group(1) : "";
			// deal with escaped commas and escapes of escaped backslash characters
			int i = 0;
			while (p.endsWith("\\")){
				if (i % 2 == 1){ // remove the first, then one on two for escaped backslashes
					p = p.substring(0, p.length() - 1);
				}
				i++;
			}
			if (i % 2 == 1){
				p += ","; // escaped comma
			} else {
				ret.add(p);
				p = "";
			}
		}
		if (p != ""){
			ret.add(p);
		}
		return ret.toArray(new String[0]);
	}


	public static class FormatErrorException extends Exception {

		/** stupid constructor */
		public FormatErrorException(String string) {
			super(string);
		}

		/**
		 * eclipse wants this...
		 */
		private static final long serialVersionUID = -1089861148505190177L;
	}
	
	public static class RuntimeFormatErrorException extends RuntimeException {

		/** stupid constructor */
		public RuntimeFormatErrorException(String string) {
			super(string);
		}

		/**
		 * eclipse wants this...
		 */
		private static final long serialVersionUID = -6222454106319984513L;
	}
}
