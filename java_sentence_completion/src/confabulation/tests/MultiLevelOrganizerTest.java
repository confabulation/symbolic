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

import static confabulation.tests.ConfabulationStubTest.assertArrayEqualsDBG;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import utils.ArrayTools;

import confabulation.MultiLevelOrganizer;
import confabulation.SymbolConverter;
import confabulation.SymbolMapping;

public class MultiLevelOrganizerTest {

	private MultiLevelOrganizer morg;
	private SymbolConverter syc;

	@Before
	public void setUp() throws Exception {
		syc = new SymbolConverter();

		int[] levels = new int[] { 2, 2 };

		String[] voc_below = { "a", "b", "c" };
		SymbolMapping below = new SymbolMapping();
		insert_vocab(below, voc_below);

		String[] voc_above = Arrays.copyOf(voc_below, voc_below.length + 1);
		voc_above[voc_below.length] = syc
				.string_symbol(new String[] { "a", "c" });
		SymbolMapping above = new SymbolMapping();
		insert_vocab(above, voc_above);

		SymbolMapping[] mappings = new SymbolMapping[] { below, above };
		morg = new MultiLevelOrganizer(levels, mappings, ArrayTools.new_fill(2,
				syc));
	}

	public static void insert_vocab(SymbolMapping sm, String[] vocabulary) {
		for (String symb : vocabulary) {
			sm.add_symbol(symb);
		}
	}

	@Test
	public void organize() {
		String[][] expected = { { "a" }, { null }, { "a" }, { null } };
		String[][] actual;

		actual = morg.organize(new String[] { "a" });
		assertArrayEqualsDBG(expected, actual);

		String[][] expected1 = { { "a" }, { "b" }, { "a" }, { "b" } };
		actual = morg.organize(new String[] { "a", "b" });
		assertArrayEqualsDBG(expected1, actual);

		String[][] expected2 = { { "a" }, { "c" },
				{ syc.string_symbol(new String[] { "a", "c" }) }, { null } };
		actual = morg.organize(new String[] { "a", "c" });
		assertArrayEqualsDBG(expected2, actual);
	}

}
