package de.mrkriskrisu.bot;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KrackelUtils {

	public static class Logger {

		private static boolean debug = false;
		private static PrintWriter out;

		private static void checkLogger() {
			if (out != null)
				return;
			try {
				out = new PrintWriter(System.currentTimeMillis() + ".txt");
			} catch (FileNotFoundException e) {
				Logger.error(e);
			}
		}

		public static void log(String log) {
			Logger.checkLogger();
			handle(LogType.INFO, log);
		}
		
		public static void debug(String log) {
			if(!debug) return;
			Logger.checkLogger();
			handle(LogType.DEBUG, log);
		}

		public static void handle(LogType type, String msg) {
			String log = getCurrentTime() + " [" + type + "] " + msg;
			System.out.println(log);
			// Save to log
			out.println(log);
			out.flush();
		}

		public static void error(Exception e) {
			handle(LogType.ERROR, e.getLocalizedMessage());
			for(StackTraceElement el: e.getStackTrace()) {
				handle(LogType.ERROR, el.toString());
			}
		}

		private static String getCurrentTime() {
			DateFormat hour = new SimpleDateFormat("HH:mm:ss");
			return hour.format(new Date(System.currentTimeMillis()));
		}
		
		public static void setDebuggingMode(boolean mode) {
			Logger.debug = mode;
			Logger.log("Debug mode " + (mode ? "activated" : "deactivated"));
		}

	}

	public enum LogType {
		DEBUG, INFO, ERROR;
	}

}
