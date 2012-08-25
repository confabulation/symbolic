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

package utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A millisecond stopwatch, with some {@link #stats(boolean)}
 * 
 * @author bernard
 */
public class Stopwatch {

	private List<String> sections;

	private LinkedList<Long> times;
	private long last_start = 0;

	/**
	 * Creates the stopwatch. Must be {@link #start()}ed
	 */
	public Stopwatch() {
		sections = new LinkedList<String>();
		times = new LinkedList<Long>();
	}

	/**
	 * Start the stopwatch
	 * 
	 * @see #start(String)
	 * @see #stop()
	 */
	public void start() {
		start("");
	}

	/**
	 * start the stopwatch, registering the result with a custom section name
	 * 
	 * @param section
	 *            the section name. If null or empty string, a default name
	 *            which includes a section number is used
	 * @see #start()
	 * @see #stop()
	 */
	public void start(String section) {
		if (is_started()) {
			stop();
		}
		if (section == null || section == "") {
			sections.add("section no." + (sections.size() + 1));
		} else {
			sections.add(section);
		}

		last_start = System.currentTimeMillis();
	}

	/**
	 * stop the stopwatch (no effect if it is already stopped)
	 * 
	 * @see #start()
	 * @see Stopwatch#start(String)
	 */
	public void stop() {
		if (is_started()) {
			times.add(System.currentTimeMillis() - last_start);
		}
	}

	/**
	 * whether stopwatch is currently running or stopped
	 */
	public boolean is_started() {
		return times.size() == sections.size() - 1;
	}

	/**
	 * Full {@link #stats(boolean)}
	 */
	public String stats(){
		return stats(true);
	}
	
	/**
	 * some stats about the stopwatch. The currently running section is ignored.
	 * 
	 * @param section_detail
	 *            include times for each section
	 */
	public String stats(boolean section_detail) {
		String ret = "";
		if (is_started()) {
			ret += "Stopwatch running. Running section ignored\n";
		}

		// sections and times
		if (section_detail) {
			ret += times_by_section(sections, times);
		}

		// min and max
		if (times.size() > 0) {
			String min_section = sections.get(0);
			long min = times.get(0);
			String max_section = min_section;
			long max = min;
			Iterator<String> sec_it = sections.iterator();
			for (long t : times) {
				String cur_sec = sec_it.next();
				if (t < min) {
					min = t;
					min_section = cur_sec;
				} else if (t > max) {
					max = t;
					max_section = cur_sec;
				}
			}
			ret += "Shortest section: " + min + "ms\t" + min_section + "\n";
			ret += "Longuest section: " + max + "ms\t" + max_section + "\n";
		}

		long total_time = get_total_time();
		ret += "Total time: " + total_time + "ms\n";
		if (times.size() > 0) {
			ret += "Average time: " + ((double) total_time / times.size())
					+ "ms\n";
		}
		return ret;
	}


	/**
	 * the total execution time
	 */
	public long get_total_time() {
		long total = 0;
		for (long t : times) {
			total += t;
		}
		return total;
	}
	

	/**
	 * TODO spec
	 * @param sections 
	 * @param times 
	 * @return
	 */
	public static String times_by_section(List<String> sections, List<Long> times){
		String ret = "";
		Iterator<String> sec_it = sections.iterator();
		for (long t : times) {
			ret += sec_it.next() + "\t" + t + "ms\n";
		}
		return ret;
	}
	
	
	/**
	 * @return the sections
	 */
	public List<String> getSections() {
		return sections;
	}

	/**
	 * @return the times
	 */
	public LinkedList<Long> getTimes() {
		return times;
	}
}
