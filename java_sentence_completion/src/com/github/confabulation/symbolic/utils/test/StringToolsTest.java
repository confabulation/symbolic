package com.github.confabulation.symbolic.utils.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.confabulation.symbolic.utils.StringTools;

public class StringToolsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void fold_test_ntimes() {
		for (int i = 0; i < 100; i++) {
			fold();
		}
	}

	public void fold() {
		String data, ret;
		int max_chars = 4;

		// * if splitting on space always succeeds and no '\n' is present
		// ` then s.equals(re.replaceAll("\n", " "))
		// Whitespace test
		int n = 22;
		data = new String(new char[n]).replaceAll("\0", " "); // repeat str
		ret = StringTools.fold(max_chars, data);
		assertEquals(
				"len(data): " + data.length() + " len(ret): " + ret.length(),
				data, ret.replaceAll("\n", " "));

		// * if splitting normally always succeeds (no word delimited
		// | by ' ' and '\n' is longer than max_chars)
		// | then len(s) == len(ret)
		// | and
		// ` s.replaceAll("\n", " ").equals(ret.replaceAll("\n", " ");
		Random ran = new Random();
		data = new String(new char[ran.nextInt(max_chars)]).replaceAll("\0",
				"x");
		for (int i = 0; i < 10; i++) {
			data += new String(new char[ran.nextInt(max_chars / 2) + 1])
					.replaceAll("\0", " ");
			data += ran.nextBoolean() ? "\n" : "";
			data += new String(new char[ran.nextInt(max_chars / 2) + 1])
					.replaceAll("\0", " ");
			data += new String(new char[ran.nextInt(max_chars) + 1])
					.replaceAll("\0", "x");
		}

		ret = StringTools.fold(max_chars, data);
		assertEquals("data:\n" + data + "\nret:\n" + ret,
				data.replaceAll("\n", " "), ret.replaceAll("\n", " "));

		// * no line in ret such that len(line) > max_chars (without the '\n')
		char[] chars = "xxxx \n".toCharArray();
		data = "";
		for (int i = 0; i < 2 * (max_chars + ran.nextInt(max_chars)); i++) {
			data += chars[ran.nextInt(chars.length)];
		}

		ret = StringTools.fold(max_chars, data);
		for (String s : ret.split("\n")) {
			assertTrue("s: " + s + "max_chars: " + max_chars + " data: " + data
					+ " ret: " + ret, s.length() <= max_chars);
		}

		// * if forced to split in word,
		// ` then chain length + 1 for each forced split
		// * corollary: if chain contains no split character ('\n' or ' ')
		// | and len(s) >= 1
		// ` then len(ret) == len(s) + (len(s) - 1) / max_chars
		data = new String(new char[ran.nextInt(max_chars * 10) + 1]).replaceAll(
				"\0", "x");
		ret = StringTools.fold(max_chars, data);
		assertEquals("data: "+ data + "\nlen(data): "+data.length()+"\nret:\n" + ret,ret.length(), data.length() + (data.length() -1 )/ max_chars);
	}

}
