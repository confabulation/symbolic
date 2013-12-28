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

import gui.GuiConsole;
import io.BufferedReader;
import io.LineStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.JFileChooser;

import parser.Tokenizer;
import parser.WordTokenizer;
import preprocessor.Preprocessor;
import utils.ArrayTools;
import utils.RuntimeIOException;
import utils.StringTools;

public class Main {

	private static String[] class_names = {//
	"ForwardConfabulation", //
			"FullMeshConfabulation", //
			"Multiconfabulation1", //
			"TwoLevelSimpleConfabulation", //
			"TwoLevelMulticonfabulationChap6", //
	};

	private static Class<?>[] classes = {//
	ForwardConfabulation.class, //
			FullMeshConfabulation.class, //
			Multiconfabulation1.class, //
			TwoLevelSimpleConfabulation.class, //
			TwoLevelMulticonfabulationChap6.class, //
	};

	private static String[] class_description = {//
			"linear architecture with only forward knowledge bases", //
			"linear architecture with a full mesh of knowledge bases between the modules", //
			"linear architecture with our first multiconfabulation algorithm", //
			"two-level architecture with the forward confabulation algorithm", //
			"two-level architecture with a multiconfabulation algorithm inferred from chapter 6 of Confabulation Theory: the Mechanism of Thought", //
	};

	public static void main(String[] args) {
		PrintStream err = System.err;
		String corpus = null;
		if (args.length == 0) {
			// TODO spawn graphical mode
			usage(0);
			GuiConsole io = new GuiConsole();
			shell(null /* TODO */, TwoLevelMulticonfabulationChap6.class, io.in, io.out, io.out);
		}

		int eopt = ArrayTools.find_equal("--", args);
		int help = ArrayTools.find_equal("--help", args);
		if (help >= 0 && (eopt > help || eopt < 0)) {
			usage(0);
		}
		if (eopt >= 0) {
			if (eopt >= args.length - 1) {
				err.println("No corpus name in arguments");
				usage(1);
			}
			corpus = args[eopt + 1];
		} else {
			eopt = args.length;
		}
		
		String opt_algo = "--algo=";
		Class<?> algo = null;
		for (int i = 0; i < eopt; i++){
			if (args[i].startsWith(opt_algo)) {
				String name = args[i].substring(opt_algo.length());
				Class<?>[] algos = ArrayTools.get_map(name, class_names, classes);

				if (algos.length == 0) {
					err.println(name
							+ " does not correspond to any known algorithm");
					err.println(StringTools.fold(80,
							"Pick one in:\n" + Arrays.deepToString(class_names)));
					err.println("or use the '--help' option");
					System.exit(1);

				} else if (algos.length > 1) {
					err.println("FATAL: duplicate entry '" + name
							+ "' in name-class table");
					System.exit(-1);
				}
				
				if (algo != null){
					err.println("Duplicate option --algo");
					usage(1);
				}
				algo = algos[0];
			} else {
				if (corpus != null){
					err.println("Multiple corpus files are not yet supported");
					usage(1);
				}
				corpus = args[i];
			}
		}
		if (algo == null){
			algo = TwoLevelMulticonfabulationChap6.class;
		}
		if (corpus == null){
			err.println("no corpus found in the arguments");
			usage(1);
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		shell(get_preprocessed_file(corpus), algo, in, System.out, err);
	}

	/**
	 * print usage message and exit
	 * 
	 * @param exit
	 *            exit with this value
	 */
	public static void usage(int exit) {
		final int termwidth = 80;
		String msg = "GNU GPL, (c) Bernard Paulus and Cédric Snauwaert\n";
		msg += "ARGUMENTS: [--help] [--algo=NAME] [--] CORPUS_FILE\n";
		msg += StringTools.fold(termwidth, "builds an architecture, learn"
				+ "and present a sentence completion shell,"
				+ " using confabulation\n");
		msg += "\n";
		msg += "--help: print this help and exit\n";
		msg += "\n";
		msg += "--algo=NAME: select the completion architecture and algorithm\n";
		msg += "    NAME must be one of:\n";
		for (int i = 0; i < class_names.length; i++) {
			msg += StringTools.fold(80, "    * " + class_names[i] + " :  "
					+ class_description[i] + "\n");
		}
		msg += "\n";
		msg += "--: end of options\n";
		msg += "\n";
		msg += "CORPUS_FILE must be a utf8 text file\n";
		System.err.println(msg);
		System.exit(0);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static void main2(String[] args) {
		Class<TwoLevelMulticonfabulationChap6> a = TwoLevelMulticonfabulationChap6.class;
		// a.getConstructor(parameterTypes)();

		// find an appropriate corpus file
		String file = "/home/bernard/tmp/memoire_confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt"; // "C:/Documents and Settings/Ced/Bureau/confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt";

		File corpus = new File(file);
		if (!corpus.canRead()) {

			System.out.println(corpus.getPath()
					+ " cannot be read. Pick one instead.");

			JFileChooser f = new JFileChooser();
			f.showOpenDialog(null);
			corpus = f.getSelectedFile();

			if (!corpus.canRead()) {
				System.err.println(corpus.getPath()
						+ " can't be read. Exiting.");
				System.exit(-1);
			}
		}
	}

	/**
	 * Return a path to a preprocessed file, or crash
	 * 
	 * @param file
	 *            a path to a file. If the extension of the filename corresponds
	 *            to a preprocessed file (ends in ".formated.txt.comma.txt"),
	 *            then preprocess the file.
	 * @return the path to the preprocessed file
	 * @throws RuntimeIOException
	 *             either file is not readable ot an error occurred while
	 *             writing the preprocessed file
	 */
	public static String get_preprocessed_file(String file)
			throws RuntimeIOException {

		File corpus = new File(file);
		if (!corpus.getName().endsWith("formated.txt.comma.txt")) {
			System.out.println("> Preprocessing file");

			try {
				return Preprocessor.preprocess(corpus.getAbsolutePath());
			} catch (FileNotFoundException e) {
				// this is really unlikely
				throw new RuntimeIOException(e);
			}
		}
		return corpus.getAbsolutePath();
	}

	public static void shell(String preprocessed_file, Class<?> algo, LineStream in, PrintStream out, PrintStream err) {

		int n_modules = 10; // 10
		out.println("max module : " + ConfConstant.Nmax);

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		starttime = System.currentTimeMillis();

		ConfabulationStub fconfab = null;
		Tokenizer tok = new WordTokenizer();
		try {
			fconfab = (ConfabulationStub) algo.getConstructor(int.class, String.class).newInstance(n_modules, preprocessed_file);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("rethrown",e);
		} catch (SecurityException e) {
			throw new RuntimeException("rethrown",e);
		} catch (InstantiationException e) {
			throw new RuntimeException("rethrown",e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("rethrown",e);
		} catch (InvocationTargetException e) {
			err.println("Error: target constructors must take (int, String) as parameters");
			e.printStackTrace();
			System.exit(-1);
		} catch (NoSuchMethodException e) {
			err.println("Error: target constructors must take (int, String) as parameters");
			e.printStackTrace();
			System.exit(-1);
		}
		endtime = System.currentTimeMillis();
		print_mem_usage();
		out.println("total time used for "
				+ fconfab.getClass().getName() + " " + (endtime - starttime)
				+ "ms");
		out.println();

		out
				.println("Enter the beginning of a line of text and the program\nwill try to find the next word.");
		out.println("Commands are :\n/quit : exit program");
		out
				.println("/setK Value : set value of K (needed input knowledge links)");
		out.println("/expectation : set expectation to true/false");
		out
				.println("/setMaxExpectation Value : set maximum value of expectation to print");
		out
				.println("/setPosition Value : the module who will undergo confabulation will be the Value after the end of the sentence");

		boolean expectation = false;
		int K = 1;
		int maxExpectation = 5;
		int position = 1;

		String CurLine = ""; // Line read from standard in
		while (true) {
			try {
				CurLine = in.readLine();
				CurLine = CurLine == null ? null : CurLine.trim();
				if (CurLine == null || CurLine.equals("/quit")) { // exits on
																	// EOF
																	// (ctrl+D)
					out.println("Exiting!");
					out.close();
					break;
				} else if (CurLine.equals("/expectation")) {
					if (expectation == false)
						expectation = true;
					else
						expectation = false;
					out.println("---Notice---\nExpectation has been set to "
									+ expectation);
				} else if (CurLine.startsWith("/setK")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						if (tempK >= -1 && tempK <= ConfConstant.Nmax) {
							K = tempK;
							// forward confab
							fconfab.setK(K);

							out.println("---Notice---\nK has been changed to "
											+ K);
						} else {
							err.println("Invalid K. Must be in [-1, "
									+ ConfConstant.Nmax + "]");
						}
					}
				} else if (CurLine.startsWith("/setMaxExpectation")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						maxExpectation = tempK;
						out
								.println("---Notice---\nmaxExpectation has been changed to "
										+ maxExpectation);
					}
				} else if (CurLine.startsWith("/setPosition")) {
					String[] cut = CurLine.split(" ");
					if (cut.length != 2)
						out.println("---Notice---\nBad input");
					else {
						int tempK = Integer.parseInt(cut[1]);
						position = tempK;
						out
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
							out.println("Expectations:");
							for (String str : expectations) {
								out.println(" - " + str);
							}
						} else {
							String next_word = fconfab.next_word(symbols, -1);
							out.println(fconfab.getClass().getName()
									+ ": " + next_word);
						}
						endtime = System.currentTimeMillis();
						out.println("time taken for "
								+ fconfab.getClass().getName() + " : "
								+ (endtime - starttime));
					} else {
						out.println(check_argument);
					}

					// try {
					// String[] answer = conf.find_next_word(CurLine,
					// expectation, maxExpectation, position);
					// for (int i = 0; i < answer.length; i++) {
					// out.println(" - " + answer[i]);
					// }
					// out.println("");
					// out.println("next 3 words without multiconfabulation :\n"+conf.get_following_words(3,
					// CurLine));
					// starttime = currentTimeMillis();
					// conf.multiconf(3, CurLine);
					// endtime = currentTimeMillis();
					// out.println("time taken for multiconfabulation : "+(endtime-starttime));
					// starttime = currentTimeMillis();
					// conf.multiconf2(3, CurLine);
					// endtime = currentTimeMillis();
					// out.println("time taken for multiconfabulation 2 : "+(endtime-starttime));
					// //conf.find_several_words(2, CurLine);
					// } catch (ConfabulationException e) {
					// // TODO Auto-generated catch block
					// //e.printStackTrace();
					// out.println(e);
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
