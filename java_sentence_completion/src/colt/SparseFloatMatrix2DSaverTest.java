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
package colt;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import cern.colt.matrix.tfloat.impl.SparseFloatMatrix2D;
import colt.SparseFloatMatrix2DSaver.FormatErrorException;

/**
 * @author bernard and cedric
 *
 */
public class SparseFloatMatrix2DSaverTest {

	@Test
	public void test_write() {
		int nrows = 10;
		int ncolumns = 5;
		SparseFloatMatrix2D m = new SparseFloatMatrix2D(nrows, ncolumns);
		m.set(0, 0, 1);
		m.set(1, 1, 1);
		File f = new File("test_write"+ new Random().nextLong() + ".txt");
		SparseFloatMatrix2DSaver.write(m, f.getPath());
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			assertEquals("SparseFloatMatrix2D (" + nrows + "," + ncolumns +")", in.readLine());
			assertEquals("0,0,1.0", in.readLine());
			assertEquals("1,1,1.0", in.readLine());
			assertEquals("SparseFloatMatrix2D END", in.readLine());
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("This shouldn't happen");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError("This shouldn't happen");
		} finally {
			f.delete();
		}
	}
	
	@Test
	public void test_read(){
		int nrows = 10;
		int ncolumns = 5;
		SparseFloatMatrix2D m = new SparseFloatMatrix2D(nrows, ncolumns);
		m.set(0, 0, 1);
		m.set(1, 1, 1);
		File f = new File("test_read"+ new Random().nextLong() + ".txt");
		SparseFloatMatrix2DSaver.write(m, f.getPath());
		try {
			assertEquals(m, SparseFloatMatrix2DSaver.read(f.getPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError(f.getPath() + " not found");
		} catch (FormatErrorException e) {
			e.printStackTrace();
			throw new AssertionError(f.getPath() + " has wrong format");
		} finally {
			f.delete();
		}
	}
}
