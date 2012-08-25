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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

/**
 * Parse file, putting a single sentence on a single line
 * 
 * @author bernard TODO: deal with all the cases of dots used in the middle of a
 *         sentence: abbreviations, numbers, urls, ...
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// removeBOM()
//		 JFileChooser f1 = new JFileChooser();
//		 f1.showOpenDialog(null);
//		 File file1 = f1.getSelectedFile();
//		 removeBOM(file1.getAbsolutePath());
//		 System.exit(0);

		FileInputStream fis;
		FileOutputStream fos;
		FileOutputStream fts;
		BufferedReader stream;
		BufferedWriter outstream;
		BufferedWriter tempstream;
		String file = "C:/Users/Kerwyn/Desktop/pg17989.txt";

		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			/* file doesn't exist */
			System.err.println(file + " doesn't exists. Pick one instead.");
			JFileChooser f = new JFileChooser();
			f.showOpenDialog(null);
			file = f.getSelectedFile().getPath();
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				System.err.println(file + " doesn't exist either. Exiting.");
				return;
			}
		}
		String tempfile = file + ".temp.txt";
		String outfile = file + ".formated.txt";
		try {
			/* analyse file for abbreviations */
			File f = new File(file);
			Set<String> abbr = find_simple_regex_abbreviations(f);
			abbr.addAll(find_abbreviations(f));

			fos = new FileOutputStream(outfile);
			fts = new FileOutputStream(tempfile);

			// Here BufferedInputStream is added for fast reading.
			// bis = new BufferedInputStream(fis);
			// stream = new DataInputStream(bis);

			stream = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			outstream = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			tempstream = new BufferedWriter(
					new OutputStreamWriter(fts, "UTF-8"));

			String line = "";
			while ((line = stream.readLine()) != null) {
				// if (line.l)
				tempstream.write(line);
				tempstream.write(" ");
			}
			stream.close();
			fis.close();
			tempstream.close();
			fts.close();
			fis = new FileInputStream(tempfile);
			stream = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

			String footnotes = "\\(\\d+\\)|\\[\\d+\\]|<\\d+>";
			Pattern to_suppress = Pattern.compile("(\"|\\\\|--|—|–|»|«|“|”|„|"
					+ footnotes + ")");
			String tirets = "((^|\\s)-(\\s-)*(\\s|$))";
			Pattern to_whitespace = Pattern.compile(tirets);

			// custom_to_replace[i] -> custom_replacement[i]
			String[] custom_to_replace = new String[] {
					// collated letters
					"œ", "Œ", "æ", "Æ",
					// (semi)colons ( + add whitespaces)
					",", ";",
					// apostrophes
					"’", "‘", "´",
					// sentence ends
					"…",
					// non-footnote parenthesis
					"\\(", "\\)",
					// non-footnote brackets
					"\\[", "\\]", };
			String[] custom_replacement = new String[] {
					// collated letters
					"oe", "Oe", "ae", "Ae",
					// (semi)colons ( + add whitespaces)
					" , ", " , ",
					// apostrophes
					"'", "'", "'",
					// sentence ends
					"...",
					// non-footnote parenthesis
					" ( ", " ) ",
					// non-footnote brackets
					" [ ", " ] ", };
			Pattern[] custom_patterns = new Pattern[custom_to_replace.length];
			for (int i = 0; i < custom_to_replace.length; i++) {
				custom_patterns[i] = Pattern.compile(custom_to_replace[i]);
			}

			Pattern sentence_end = Pattern.compile("(!|\\?|(\\.)+)$");

			line = "";
			while ((line = stream.readLine()) != null) {
				Matcher suppressor = to_suppress.matcher(line);
				line = suppressor.replaceAll("");
				Matcher whiter = to_whitespace.matcher(line);
				line = whiter.replaceAll(" ");
				// replace all oe, ae..
				for (int i = 0; i < custom_patterns.length; i++) {
					line = custom_patterns[i].matcher(line).replaceAll(
							custom_replacement[i]);
				}
				String[] cutline = line.split(" ");

				for (String word : cutline) {
					if (word.length() == 0) {
						// skip empty string
					} else if (abbr.contains(word)) {
						outstream.write(word + " "); // print abbreviations
														// as-is
					} else {
						Matcher m = sentence_end.matcher(word);
						if (m.find()) {
							/* end of sentence */
							String punctuation = m.group();
							outstream.write(word.substring(0,
									word.length() - punctuation.length())
									.toLowerCase()
									+ " ");
							/* if punctuation is !, ?, ., or '...' */
							if (punctuation.length() == 1
									|| punctuation.length() == 3) {
								outstream.write(punctuation);
							} else {
								// things like '..' or '....'
								outstream.write(".");
							}
							outstream.newLine();
						} else {
							outstream.write(word.toLowerCase() + " ");
						}
					}
				}
				outstream.newLine();
			}

			stream.close();
			fis.close();
			outstream.close();
			fos.close();
			System.out.println("done writing everything in lowercase");
			removeuseless(outfile, tempfile);
			usecomma(tempfile, outfile + ".comma.txt", ",");
			System.out.println("task finished");
		} catch (FileNotFoundException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void removeuseless(String filename, String outfilename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			FileOutputStream fos = new FileOutputStream(outfilename);

			BufferedReader stream = new BufferedReader(new InputStreamReader(
					fis, "UTF-8"));
			BufferedWriter outstream = new BufferedWriter(
					new OutputStreamWriter(fos, "UTF-8"));

			String line = "";
			while ((line = stream.readLine()) != null) {
				// if (line.l)
				outstream.write(line.replaceAll("--", ""));
				outstream.newLine();
			}
			stream.close();
			outstream.close();
			fis.close();
		} catch (FileNotFoundException e1) {
			System.err.println(filename + " doesn't exist either. Exiting.");
			System.exit(0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void usecomma(String filename, String outfilename,
			String charactersplit) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			FileOutputStream fos = new FileOutputStream(outfilename);

			BufferedReader stream = new BufferedReader(new InputStreamReader(
					fis, "UTF-8"));
			BufferedWriter outstream = new BufferedWriter(
					new OutputStreamWriter(fos, "UTF-8"));

			String line = "";
			while ((line = stream.readLine()) != null) {
				String[] cutline = line.split(charactersplit);
				outstream.write(line.trim());
				outstream.newLine();
				String templine = "";
				if (cutline.length > 1) {
					for (int i = 1; i < cutline.length; i++) {
						outstream.write(cutline[i].trim());
						outstream.newLine();
						/*
						 * templine = cutline[i]; for (int
						 * j=i+1;j<cutline.length;j++){ templine =
						 * templine+" , "+cutline[j]; outstream.write(templine);
						 * outstream.newLine(); } templine="";
						 */
					}
				}
				// if (line.l)

			}
			stream.close();
			outstream.close();
			fis.close();
		} catch (FileNotFoundException e1) {
			System.err.println(filename + " doesn't exist either. Exiting.");
			System.exit(0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find the abbreviations by polling on expressions found by regular
	 * expressions.
	 * 
	 * @param file
	 * @return
	 */
	public static Set<String> find_abbreviations(File file) {
		if (!file.canRead()) {
			throw new IllegalArgumentException(file
					+ " is non-existent or not readable");
		}
		String[] potentials = find_potential_abbreviations(file).toArray(
				new String[] {});
		Set<String> confirmed_abbreviations = new HashSet<String>();
		// cut the last dot off each potential abbreviation
		for (int i = 0; i < potentials.length; i++) {
			// System.out.println(potentials[i]);
			potentials[i] = potentials[i].substring(0,
					potentials[i].length() - 1);
		}
		// build a regular expression from it
		String regexpr = "\\b(";
		for (String str : potentials) {
			regexpr += str.replaceAll("\\.", "\\\\.") + "|"; // escape dots
		}
		regexpr = regexpr.substring(0, regexpr.length() - 1) + ")\\b";
		// and pray the java regex engine is clever enough to combine the
		// alternatives in a prefix tree
		Pattern p = Pattern.compile(regexpr);
		// count the proportion of those that end with a dot
		HashMap<String, Double> proportions_dot = new HashMap<String, Double>(
				potentials.length);
		HashMap<String, Integer> total_occurences = new HashMap<String, Integer>(
				potentials.length);
		for (String str : potentials) {
			proportions_dot.put(str, 0.0);
			total_occurences.put(str, 0);
		}
		try {
			BufferedReader f = new BufferedReader(new FileReader(file));
			String line;
			while ((line = f.readLine()) != null) {
				Matcher m = p.matcher(line);
				while (m.find()) {
					double old_prop = proportions_dot.get(m.group());
					int old_total = total_occurences.get(m.group());
					int new_total = old_total + 1;
					// update proportions of dot/non dot
					if (line.length() > m.end() && line.charAt(m.end()) == '.') {
						proportions_dot.put(m.group(), old_prop * old_total
								/ new_total + 1.0 / new_total);
						total_occurences.put(m.group(), new_total);
					} else {
						proportions_dot.put(m.group(), old_prop * old_total
								/ new_total);
						total_occurences.put(m.group(), new_total);
					}
				}
			}
			for (String str : potentials) {
				if (proportions_dot.get(str) > 0.9
						&& (total_occurences.get(str) > 1 || str.contains("."))) {
					// if only one occurrence: don't include
					// Names supposed to be known by everyone
					// (ex: countries) can be the last word of a line
					// Still include if there is other dots in the word
					confirmed_abbreviations.add(str + ".");
				}
			}
		} catch (FileNotFoundException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return confirmed_abbreviations;
	}

	/**
	 * Find all the potential dotted abbreviations by simple regular expression
	 * 
	 * @param file
	 * @return
	 */
	public static Set<String> find_potential_abbreviations(File file) {
		if (!file.canRead()) {
			throw new IllegalArgumentException(file
					+ " is non-existent or not readable");
		}
		// Pattern explanation:
		// find all the expressions that either are a
		// single dotted lowercase alphabetic character, or an uppercase
		// character followed by any string of alphabetic characters,
		// both ending with a dot
		// Examples:
		// Mr. -> one match
		// i.e. -> two matches
		Pattern p = Pattern.compile("\\b([A-Z][a-zA-Z]{1,5})\\.");
		Pattern blank_line = Pattern.compile("^\\s*$");
		// i guess above 6 characters long it's too much for a dotted
		// abbreviation

		Set<String> potentials = new HashSet<String>();
		Set<String> black_list = new HashSet<String>();
		try {
			BufferedReader f = new BufferedReader(new FileReader(file));
			String line;
			int last_length = 0;
			int last_end = 0;
			Matcher m_blank = null;
			String last_match = "";
			while ((line = f.readLine()) != null) {
				// if line is blank, and there was a suspected word at the end
				// of the line,
				// then the ending dot was the end of a sentence.
				// And thus the last suspected word can be ruled out.
				if ((!last_match.equals("")) && last_end == last_length
						&& (m_blank = blank_line.matcher(line)) != null
						&& m_blank.find()) {
					black_list.add(last_match);
					last_match = "";
					last_length = 0;
				} else {
					Matcher m = p.matcher(line);
					// find all the matches and put back multi-dots
					// abbreviations
					// together
					last_match = "";
					last_end = 0;
					String possible_abbreviation = "";
					while (m.find()) {
						if (last_end != m.start()) { // this abbreviation is not
														// a
														// continuation from the
														// last one
							if (!possible_abbreviation.equals("")) {
								potentials.add(possible_abbreviation);
								last_match = possible_abbreviation;
								possible_abbreviation = "";
							}
							if (m.end() >= line.length()
									|| line.charAt(m.end()) != '.') {
								possible_abbreviation = m.group();
							}
						} else {
							possible_abbreviation += m.group();
						}
						last_end = m.end();
					}
					if (last_end == 0 && possible_abbreviation.equals("")) {
						// System.out.println("no abbreviation was found");
					} else if (!possible_abbreviation.equals("")) { // add last
																	// match
						potentials.add(possible_abbreviation);
						last_match = possible_abbreviation;
						possible_abbreviation = "";
					}
					last_length = line.length();
				}
			}
		} catch (FileNotFoundException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		potentials.removeAll(black_list);
		return potentials;
	}

	/**
	 * Find potential abbreviations throught multiple regular expressions
	 * 
	 * @param f
	 * @return
	 */
	public static Set<String> find_simple_regex_abbreviations(File file) {
		if (!file.canRead()) {
			throw new IllegalArgumentException(file
					+ " is non-existent or not readable");
		}
		Set<String> potentials = new HashSet<String>();
		String[] patterns = new String[] {

				// match forms like 'i.e.', U.S.A.
				"\\b([a-zA-Z]\\.)+",
				// this would also match F.u.c.k.

				// match forms like 3.14159, 127.0.0.1...
				// the 6.022 in +6.022E23,
				// this is wider than strict abbreviations,
				// but we want remove parasitic dots
				"((\\d)+\\.)+(\\d)+", };
		// combine all the regexes
		String final_pattern = "(";
		for (String str : patterns) {
			final_pattern += str + "|";
		}
		final_pattern = final_pattern.substring(0, final_pattern.length() - 1)
				+ ")";
		Pattern p = Pattern.compile(final_pattern);

		try {
			BufferedReader f = new BufferedReader(new FileReader(file));
			String line;
			while ((line = f.readLine()) != null) {
				Matcher m = p.matcher(line);
				while (m.find()) {
					potentials.add(m.group(1));
				}
			}
		} catch (FileNotFoundException e) {
			// this shouldn't happen
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return potentials;
	}

	private static final int BUFFER_LENGTH = 65536;
	private static final int EOF = -1;

	/**
	 * remove the useless windows UTF8 byte-order mark from a file
	 * 
	 * @param filename
	 * @throws RuntimeIOException
	 *             because you probably don't give a damn about catching this
	 *             exception. However, if you care, you can catch it! I added a
	 *             throws specially for you, so you know what you gotta catch :)
	 */
	public static void removeBOM(String filename) throws RuntimeIOException {
		// inspired from http://www.ueber.net/who/mjl/projects/bomstrip/
		final byte[] BOM = { (byte) 0xef, (byte) 0xbb, (byte) 0xbf };
		byte[] bomBuffer = new byte[BOM.length];
		byte[] buf = new byte[BUFFER_LENGTH];

		try {
			File f = File.createTempFile("removeBOM",
					"" + new Random().nextLong());

			InputStream in = new FileInputStream(filename);

			int nread;
			if ((nread = in.read(bomBuffer, 0, bomBuffer.length)) != EOF) {
				if (Arrays.equals(bomBuffer, BOM)) { // BOM present

					// copy remaining file contents to tmp file
					OutputStream tmp = new FileOutputStream(f);
					while ((nread = in.read(buf, 0, buf.length)) != EOF) {
						tmp.write(buf, 0, nread);
					}

					InputStream tmp2 = new FileInputStream(f);
					OutputStream orig = new FileOutputStream(filename);

					// avoid race condition: close after old fds
					in.close();
					tmp.close();

					// copy back data
					while ((nread = tmp2.read(buf, 0, buf.length)) != EOF) {
						orig.write(buf, 0, nread);
					}
					tmp2.close();
					orig.close();
				} else {
					in.close(); // do nothing
				}
			}
			f.delete();
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}
}
