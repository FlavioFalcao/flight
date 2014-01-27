package flight.global;

import java.io.PrintStream;
import java.util.Formatter;

/**
 * Provides static, thread-safe text IO for logging, debugging, and error
 * reporting.
 * 
 * @author Colby Horn
 */
public class Logger {

	private static PrintStream	out	= System.out;
	private static PrintStream	err	= System.err;

	/**
	 * Log a formatted message to the standard output text steam.
	 * 
	 * @see Formatter
	 * 
	 * @param output
	 *            the format string to be logged
	 * @param args
	 *            the variable number of formatting arguments
	 * 
	 */
	public static synchronized void logOutput(String output, Object... args) {
		out.format(output, args);
		out.println();
	}

	/**
	 * Log a formatted message to the standard error text steam.
	 * 
	 * @see Formatter
	 * 
	 * @param error
	 *            the format string to be logged
	 * @param args
	 *            the variable number of formatting arguments
	 * 
	 */
	public static synchronized void logError(String error, Object... args) {
		err.format(error, args);
		err.println();
	}

}
