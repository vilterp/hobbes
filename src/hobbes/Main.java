package hobbes;

import hobbes.interpreter.Debugger;
import hobbes.interpreter.Interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

	private static final String HELP = "usage: hobbes <file> <args>\n" + 
							"-d    debug\n" +
							"-g    verbose garbage collection\n" +
							"-h    help";
	
	public static void main(String[] args) {
		// parse args
		HashSet<String> options = new HashSet<String>();
		String fileName = null;
		for(String arg: args) {
			if(arg.startsWith("-"))
				options.add(arg.substring(1));
			else if(fileName == null)
				fileName = arg;
			else {
				System.out.println("Invalid args: more than one non-option argument");
				printHelp();
				return;
			}
		}
		// go
		if(options.contains("h")) {
			printHelp();
		} else if(fileName == null) { // interactive console
			if(options.contains("d"))
				System.out.println("Didn't supply a file name to debug");
			else
				interactiveConsole(options.contains("g"));
		} else if(!options.contains("d")) {
			runFile(fileName,options.contains("g"));
		} else if(options.contains("d")) {
			debugFile(fileName,options.contains("g"));
		} else {
			System.out.println("Invalid args");
			printHelp();
		}
	}

	private static void interactiveConsole(boolean vgc) {
		Scanner s = new Scanner(System.in);
		Interpreter i = new Interpreter("<console>",vgc,false);
		while(true) {
			if(i.needsMore())
				System.out.print(" " + i.getLastOpener() + " ");
			else
				System.out.print(">> ");
			try {
				i.add(s.nextLine());
				if(!i.needsMore()) {
					String result = i.getResult();
					if(result != null)
						System.out.println("=> " + result);
				}
			} catch(NoSuchElementException e) {
				return;
			}
		}
	}

	private static void runFile(String fileName, boolean vgc) {
		File f = new File(fileName);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			fileNotFound(fileName);
			return;
		}
		Interpreter i = new Interpreter(fileName,vgc,true);
		while(s.hasNext()) {
			i.add(s.nextLine());
			if(!i.needsMore())
				i.getResult();
		}
		if(i.needsMore())
			unexpectedEOF(fileName,i.getLastOpener());
	}
	
	private static void debugFile(String fileName, boolean contains) {
		File f = new File(fileName);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			fileNotFound(fileName);
			return;
		}
		Debugger d = new Debugger(fileName);
		while(s.hasNext()) {
			d.addLine(s.nextLine());
		}
		d.go();
		if(d.needsMore())
			unexpectedEOF(fileName,d.getLastOpener());
	}

	private static void printHelp() {
		System.out.println(HELP);
	}

	private static void unexpectedEOF(String fileName, String waitingOn) {
		System.err.println("Unexpected end of file in "
				+ "\"" + fileName + "\": "
				+ "still waiting to close " + waitingOn);
	}
	
	private static void fileNotFound(String fileName) {
		System.err.println("File \"" + fileName + "\" not found");
	}
	
}
