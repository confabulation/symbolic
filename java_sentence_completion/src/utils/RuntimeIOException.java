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
