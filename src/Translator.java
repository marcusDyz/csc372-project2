import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.StringDatatypeValidator;

// Author: YD
public class Translator {
	private Pattern var_assign = Pattern.compile("^(.+) = (.+)\\.$");
	private Pattern print = Pattern.compile("^print (.+)$");
	private Pattern if_check = Pattern.compile("^if (.+)$");
	private Pattern then_check = Pattern.compile("^then (.+)$");
	private Pattern else_check = Pattern.compile("else (.+)$");
	private Pattern loop = Pattern.compile("^while (.+) ($");
	private Pattern comparator = Pattern.compile("^>=|<=|==$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern strVal = Pattern.compile("^\"\\w+\"$");
	private Pattern var = Pattern.compile("^\\w+$");
	private Pattern op = Pattern.compile("[+-*/%]");
	private Pattern bool = Pattern.compile("^TRUE|FALSE$");
	
	public static void main(String[] args) {
		if (args.length == 0) { // Interactive system
			// TODO adding explicit parsing feature into Interactive system
			Scanner scanner = new Scanner(System.in);
			System.out.print(">> ");
			String input = scanner.nextLine();
			while (!input.equals("exit")) { 
				// TODO Parse the input string and write it in Java code then print the output.
				System.out.print(">> ");
				input = scanner.nextLine();
			}
			System.out.println("Bye!");
			
		}else if (args.length == 1) { // Reading file from StdIn
			try {
				Scanner scanner = new Scanner(new File(args[0]));
				String output_filename = (args[0].split(".",0))[0] + ".java";
				output_filename = output_filename.substring(0, 1).toUpperCase() + output_filename.substring(1);
				File output = new File(output_filename);
				if (output.createNewFile()) {
			        System.out.println("File created: " + output.getName());
			      } else {
			        System.out.println("File already exists.");
			      }
				while (scanner.hasNextLine()) {
					// TODO Parse every line and translate to a Java code file.
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else { // Accept command line arguments
			
		}
		
	}
	
	// Private parsing method
	private void parseCmd(String cmd, File out) {
		
	}
	
	
	private boolean varAssign(String cmd, boolean print) {
		Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if(m.find()) {
		match = true;
		match = match && var(m.group(1), print);
		match = match && val(m.group(2), print);
		}
		printMsg(match, "<var_assign>", cmd, "variable assignment statement");
		return match;
	}
	
	private boolean print(String cmd, boolean print) {
		
	}


	private boolean if_check(String cmd, boolean print) {
		
	}
	
	private boolean then_check(String cmd, boolean print) {
		
	}
	
	private boolean else_check(String cmd, boolean print) {
		
	}
	
	private boolean comparator(String cmd, boolean print) {
		
	}
	
	private boolean var(String cmd, boolean print) {
		Matcher m = var.matcher(cmd);
		boolean match = m.find();
		if (print) 
			printMsg(match, "<var>", cmd, "variable");
		return match;
	}
	
	private boolean val(String cmd, boolean print) {
		Matcher m = intVal.matcher(cmd);
		boolean match = m.find();
		String result = "";
		if(match && print) {
			printMsg(match, "<int>", cmd, "integer");
		}else if (strVal.matcher(cmd).find() && print) {
			printMsg(match, "<String>", cmd, "string");
		}else {
			m = bool.matcher(cmd);
			match = m.find();
			if(match && print)
				printMsg(match, "<bool>", cmd, "boolean");
			else {
				m = var.matcher(cmd);
				match = m.find();
				if(match && print)
					printMsg(match, "<var>", cmd, "variable");
			}
		}
		printMsg(match, "<val>", cmd, "value");
		return match;
	}
	
	private boolean op(String cmd, boolean print) {
		
	}
		
	private static void printMsg(boolean match, String ntName, String cmd, String item) {
		if(match)
			System.out.println(ntName + ": " + cmd);
		else
			System.out.println("Failed to parse: {" + cmd + "} is not a valid " + item + ".");
	}

}
