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
package confabulation.tests.twolevelsimpleconfab_advanced;

import static org.junit.Assert.*;

import java.io.File;

import javax.swing.JFileChooser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import confabulation.TwoLevelSimpleConfabulation;

/**
 * @author bernard and cedric
 * 
 */
public class TwoLevelSimpleConfabulationTestOnMilleEtUneNuitComplete {

	private static TwoLevelSimpleConfabulation c;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String file = "/home/bernard/tmp/memoire_confabulation/corpus/milleetunenuit_complete.txt.formated.txt.comma.txt"; // "C:/Documents and Settings/Ced/Bureau/confabulation/corpus/milleetunenuit1.txt.formated.txt.comma.txt";
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

		c = new TwoLevelSimpleConfabulation(10, corpus_path);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void bug_multiword_at_end_of_given_modules() {
		// the original bug generator
		String[] symbols = {"il", "ne", "put", "repartir"};
		
		String result = c.next_word(symbols , -1);
		/* old result: */
		// Exception in thread "main" java.lang.NullPointerException
		// at
		// confabulation.TwoLevelSimpleConfabulation.multiword_symbol_treatment(TwoLevelSimpleConfabulation.java:262)
		// at
		// confabulation.TwoLevelSimpleConfabulation.conservative_rerepr(TwoLevelSimpleConfabulation.java:238)
		// at
		// confabulation.TwoLevelSimpleConfabulation.next_word(TwoLevelSimpleConfabulation.java:158)
		// at confabulation.Main.main(Main.java:193)
		/* solved <3 */
		
		// "contraindre" => this is a shitty result
		// TODO explain the shitty result!!!
		assertEquals("contraindre", result);
	}
	
	@Test
	public void chroniques_des_sassanides() {
		String[] symbols = "les chroniques des sassanides".split(" "); 
		String result = c.next_word(symbols , -1);
		// shitty answer
		assertEquals("arriva", result);
	}

}
