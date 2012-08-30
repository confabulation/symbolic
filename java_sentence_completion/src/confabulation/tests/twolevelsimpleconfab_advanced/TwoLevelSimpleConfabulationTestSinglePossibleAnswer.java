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

package confabulation.tests.twolevelsimpleconfab_advanced;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Check on a special corpus: sentences with unique four starting words.
 * <p>
 * the corpus was extracted from the treated "hacker_how2.txt" corpus by the following command-line
 * </p>
 * <p>
 * cat hacker_how2.txt.formated.txt.comma.txt | awk 'NF > 4 && !x[$1, $2, $3,
 * $4] {x[$1, $2, $3, $4] = 1; print $1, $2, $3, $4, $5}' | grep -v
 * "[^[:alpha:] ']" | head -n 100 | awk '{print "\""$0"\","}
 * </p>
 * TODO implement test!!!!
 * 
 * @author bernard and cedric
 * 
 */
public class TwoLevelSimpleConfabulationTestSinglePossibleAnswer {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void test() {
		fail("Not yet implemented");
	}

}
