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

package confabulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;

import parser.Tokenizer;
import parser.WordTokenizer;
import utils.RuntimeIOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static void main(String[] args) throws SecurityException,
			IOException {

		int n_modules = 10; // 10
		System.out.println("max module : " + ConfConstant.Nmax);

		// find an appropriate corpus file
		String file = "/home/bernard/tmp/memoire_confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt"; // "C:/Documents and Settings/Ced/Bureau/confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt";
		// String file =
		// "/home/bernard/tmp/memoire_confabulation/corpus/simple_conf_killer.txt";
		// //
		File corpus = new File(file);
		if (!corpus.canRead()) {
			JFileChooser f = new JFileChooser();
			f.showOpenDialog(null);
			corpus = f.getSelectedFile();
			System.out.println(corpus);
			if (!corpus.canRead()) {
				System.err.println(corpus.getPath()
						+ " can't be read. Exiting.");
			}
		}
		String corpus_path = corpus.getAbsolutePath();

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		starttime = System.currentTimeMillis();

		ConfabulationStub fconfab = null;
		Tokenizer tok = new WordTokenizer();
		try {
			// fconfab = new ForwardConfabulation(n_modules, corpus_path);

			// fconfab = new FullMeshConfabulation(n_modules, corpus_path);

			// fconfab = new Multiconfabulation1(n_modules, corpus_path, tok);

			// fconfab = new TwoLevelSimpleConfabulation(n_modules,
			// corpus_path);

			fconfab = new TwoLevelMulticonfabulationChap6(n_modules,
					corpus_path);
		} catch (IOException e1) {
			throw new RuntimeIOException(e1); // don't wanna bother with it
		}
		endtime = System.currentTimeMillis();
		print_mem_usage();
		System.out.println("total time used for " + fconfab.getClass().getName()
				+ " "+ (endtime - starttime) + "ms");
		System.out.println();

		System.out
				.println("Enter the beginning of a line of text and the program\nwill try to find the next word.");
		System.out.println("Commands are :\n/quit : exit program");
		System.out
				.println("/setK Value : set value of K (needed input knowledge links)");
		System.out.println("/expectation : set expectation to true/false");
		System.out
				.println("/setMaxExpectation Value : set maximum value of expectation to print");
		System.out
				.println("/setPosition Value : the module who will undergo confabulation will be the Value after the end of the sentence");

		boolean expectation = false;
		int K = 1;
		int maxExpectation = 5;
		int position = 1;

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		String CurLine = ""; // Line read from standard in
		while (true) {
			try {
				CurLine = in.readLine();
				CurLine = CurLine == null ? null : CurLine.trim();
				if (CurLine == null || CurLine.equals("/quit")) { // exits on
																	// EOF
																	// (ctrl+D)
					System.out.println("Exiting!");
					break;
				} else if (CurLine.equals("/expectation")) {
					if (expectation == false)
						expectation = true;
					else
						expectation = false;
					System.out
							.println("---Notice---\nExpectation has been set to "
									+ expectation);
				} else if (CurLine.startsWith("/setK")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						System.out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						if (tempK >= -1 && tempK <= ConfConstant.Nmax) {
							K = tempK;
							// forward confab
							fconfab.setK(K);

							System.out
									.println("---Notice---\nK has been changed to "
											+ K);
						} else {
							System.err.println("Invalid K. Must be in [-1, "
									+ ConfConstant.Nmax + "]");
						}
					}
				} else if (CurLine.startsWith("/setMaxExpectation")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						System.out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						maxExpectation = tempK;
						System.out
								.println("---Notice---\nmaxExpectation has been changed to "
										+ maxExpectation);
					}
				} else if (CurLine.startsWith("/setPosition")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						System.out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						position = tempK;
						System.out
								.println("---Notice---\nPosition of module to undergo confabulation has been changed to "
										+ position + " after end of input line");
					}
				} else {
					// forward confab
					String[] symbols = tok.parse(CurLine, "( |\t)");
					String check_argument = fconfab.check_argument(symbols, -1);
					if ("ok".equals(check_argument)) {

						starttime = System.currentTimeMillis();
						if (expectation) {
							String[] expectations = fconfab.next_expectation(
									symbols, -1);
							System.out.println("Expectations:");
							for (String str : expectations) {
								System.out.println(" - " + str);
							}
						} else {
							String next_word = fconfab.next_word(symbols, -1);
							System.out.println(fconfab.getClass().getName()
									+ ": " + next_word);
						}
						endtime = System.currentTimeMillis();
						System.out.println("time taken for "
								+ fconfab.getClass().getName() + " : "
								+ (endtime - starttime));
					} else {
						System.out.println(check_argument);
					}

					// try {
					// String[] answer = conf.find_next_word(CurLine,
					// expectation, maxExpectation, position);
					// for (int i = 0; i < answer.length; i++) {
					// System.out.println(" - " + answer[i]);
					// }
					// System.out.println("");
					// System.out.println("next 3 words without multiconfabulation :\n"+conf.get_following_words(3,
					// CurLine));
					// starttime = System.currentTimeMillis();
					// conf.multiconf(3, CurLine);
					// endtime = System.currentTimeMillis();
					// System.out.println("time taken for multiconfabulation : "+(endtime-starttime));
					// starttime = System.currentTimeMillis();
					// conf.multiconf2(3, CurLine);
					// endtime = System.currentTimeMillis();
					// System.out.println("time taken for multiconfabulation 2 : "+(endtime-starttime));
					// //conf.find_several_words(2, CurLine);
					// } catch (ConfabulationException e) {
					// // TODO Auto-generated catch block
					// //e.printStackTrace();
					// System.out.println(e);
					//
					// }

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * print the memory usage of this program
	 */
	protected static void print_mem_usage() {
		long total = Runtime.getRuntime().totalMemory();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("total memory usage: " + total + " bytes");
		System.out.println("memory really used: " + used + " bytes");
	}
}
