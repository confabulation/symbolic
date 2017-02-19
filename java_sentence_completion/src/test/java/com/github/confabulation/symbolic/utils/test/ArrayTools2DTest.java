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

package com.github.confabulation.symbolic.utils.test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.confabulation.symbolic.utils.ArrayTools2D;

public class ArrayTools2DTest {

	@Test
	public void copyOfRectangle() {
		Integer[][] value;
		Integer[][] array = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
		
		Integer[][] expected1 = {{ 8, 9}};
		value = ArrayTools2D.copyOfRectangle(array, 2, 1, 3, 3);
		assertArrayEquals(expected1, value);
		
		Integer[][] expected2 = {{1, 2}, {4, 5}};
		value = ArrayTools2D.copyOfRectangle(array, 0, 0, 2, 2);
		assertArrayEquals(expected2, value);
	}
	
	@Ignore
	@Test
	public void copyOfRectangle_advanced() {
		Integer[][] value;
		Integer[][] array = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
		
		Integer[][] expected1 = {{ 8, 9, null }, { null, null, null } };
		value = ArrayTools2D.copyOfRectangle(array, 2, 1, 3, 3);
		assertArrayEquals(expected1, value);
		
		Integer[][] expected2 = {{null, null, null}, {null, 1, 2}, {null, 4, 5}};
		value = ArrayTools2D.copyOfRectangle(array, 1, 1, -1, -1);
		assertArrayEquals(expected2, value);
	}

}
