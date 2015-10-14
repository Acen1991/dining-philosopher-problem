package common.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;

public class StreamUtils {

	/**
	 * @param args
	 */

	public static void  writeIntoStreamAndLog(PrintStream pw, BufferedWriter bw,
			String s) {
		try {
			pw.println(s);
			bw.write(s);
			bw.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void writeIntoStreamAndLog(BufferedWriter bw, String s) {
		writeIntoStreamAndLog(System.out, bw, s);
	}

}
