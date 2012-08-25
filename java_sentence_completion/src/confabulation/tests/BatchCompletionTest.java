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

package confabulation.tests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFileChooser;

import parser.WordTokenizer;
import utils.ArrayTools;

import confabulation.ConfabulationStub;
import confabulation.ForwardConfabulation;
import confabulation.SentenceCompletionIF;
import confabulation.TwoLevelMulticonfabulationChap6;
import confabulation.TwoLevelSimpleConfabulation;

/**
 * Completes many sentences at once
 * 
 * @author bernard
 * 
 */
public class BatchCompletionTest {

	public static void main(String[] args) throws IOException {
		// TIP: quick and dirty one-line test check if in corpus :devil:
		//
		// data='"puis il", "et ensuite"' ;
		// corpus="milleetunenuit_complete.txt.formated.txt.comma.txt"; sed -e
		// 's/"//g' -e 's/, /\n/g' <<< "$data" | grep -v '^[[:space:]]*$' |
		// while read -r line; do if [ "$(grep -c "^$line" "$corpus")" -gt 0 ] ;
		// then echo "IN $line"; else echo "OUT $line"; fi; done
		//
		String[] inputs = {
				// TESTS
				// in-corpus sentences starts
				"je suis", "vous êtes", "elle était", "il était", "le vizir",
				"la princesse", "le prince", "les chroniques",
				"puis il",
				"le marchand",
				// out-corpus sentences starts
				"elle aimait", "une étoile", "la servante", "le vieux",
				"la caverne", "un royaume", "un mariage", "elle épousa",
				"une histoire", "le fruit" };
		int n_words = 5;

		// find an appropriate corpus file
		String file = "/home/bernard/tmp/memoire_confabulation/corpus/milleetunenuit_complete.txt.formated.txt.comma.txt"; // "C:/Documents and Settings/Ced/Bureau/confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt";
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
		// !!! ForwardConfabulation uses K = number of previous words by default
		// ConfabulationStub confab = new ForwardConfabulation(10, corpus_path);
		// confab.setK(1);
		// SentenceCompletionIF confab = new TwoLevelSimpleConfabulation(10,
		// corpus_path);
		SentenceCompletionIF confab = new TwoLevelMulticonfabulationChap6(10,
				corpus_path);

		String[] results = multitest(confab, inputs, n_words);

		for (int i = 0; i < results.length; i++) {
			System.out.println(inputs[i] + " -> " + results[i]);
		}
	}

	/**
	 * Perform the completion of n_words to each input, using completer
	 * 
	 * @param completer
	 * @param inputs
	 * @param n_words
	 * @return the results for each of the sentences
	 */
	public static String[] multitest(SentenceCompletionIF completer,
			String[] inputs, int n_words) {
		WordTokenizer tok = new WordTokenizer();
		String[] ret = new String[inputs.length];

		for (int i = 0; i < ret.length; i++) {
			String[] input = tok.parse(inputs[i], " ");
			String[] sentence = Arrays.copyOf(input, input.length + n_words);

			for (int n = input.length; n < sentence.length;) {
				if (!"ok".equals(completer.check_argument(sentence, -1))) {
					sentence[n] = "ERROR: "
							+ completer.check_argument(sentence, -1);
					break;
				}
				String next = completer.next_word(sentence, -1);
				if (next == null) {
					sentence[n] = null;
					n++;
				} else {
					String[] out_words = next.split(" ");
					int j = 0;
					while (n < sentence.length && j < out_words.length) {
						sentence[n] = out_words[j];
						j++;
						n++;
					}
				}
			}

			ret[i] = ArrayTools
					.join(" ", Arrays.copyOfRange(sentence, input.length,
							sentence.length));
		}
		return ret;
	}

}
