package flight.global;

import java.io.PrintStream;

public class Logger {

	private static PrintStream	out	= System.out;
	private static PrintStream	err	= System.err;

	public static synchronized void logOutput(String output, Object... args) {
		out.format(output, args);
		out.println();
	}

	public static synchronized void logError(String error, Object... args) {
		err.format(error, args);
		err.println();
	}

}
