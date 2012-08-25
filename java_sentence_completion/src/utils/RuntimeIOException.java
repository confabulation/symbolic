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

/**
 * A substitution for normal {@link java.io.IOException}, when the user of the function
 * catches the exception only if he really wants to.
 * 
 * It is good practice to keep the throws clause
 * @author bernard
 */
public class RuntimeIOException extends RuntimeException {

	private static final long serialVersionUID = -1399650097256466789L;

	public RuntimeIOException() {
		super();
	}

	public RuntimeIOException(String message) {
		super(message);
	}

	public RuntimeIOException(Throwable cause) {
		super(cause);
	}

	public RuntimeIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
