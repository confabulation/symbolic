package utils.test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import utils.ArrayTools2D;

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
