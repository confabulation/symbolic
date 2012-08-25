package utils.test;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.Stopwatch;

public class StopwatchTest {

	@Test
	public void test() {
		Stopwatch sw = new Stopwatch();
		for (int k = 1; k <= 10; k++) {
			sw.start("" + k);
			for (int i = 0; i < k * 1000; i++) {
				System.out.print("");
			}
			sw.stop();
		}
		System.out.println(sw.stats(true));
	}

}
