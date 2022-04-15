import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Translator {
	private static Pattern var_assign = Pattern.compile("^(.+) = (.+)$");
	private static Pattern print_var = Pattern.compile("^print\\((.+)\\)$");
	private static Pattern print_val = Pattern.compile("^print\\(\"(.+)\"\\)$");
	private static Pattern if_check = Pattern.compile("^if (.+) \\[$");
	private static Pattern else_check = Pattern.compile("^else \\[$");
	private static Pattern loop = Pattern.compile("^while (.+) \\[$");
	private static Pattern comparative = Pattern.compile("^(.+) (.+) (.+)$");
	private static Pattern comparator = Pattern.compile("^(>|<|>=|<=|==)$");
	private static Pattern bool_expr = Pattern.compile("^\\((.+) (.) (.+)\\)$");
	private static Pattern expr = Pattern.compile("^\\((.+) (.) (.+)\\)$");
	private static Pattern end_sign = Pattern.compile("^\\]$");
	private static Pattern intVal = Pattern.compile("^\\d+$");
	private static Pattern strVal = Pattern.compile("^\"\\w+\"$");
	private static Pattern and_or = Pattern.compile("^#|\\^$");
	private static Pattern var = Pattern.compile("^\\w+$");
	private static Pattern op = Pattern.compile("^[+-/%]{1}|\\*$");
	private static Pattern bool = Pattern.compile("^TRUE|FALSE$");
	
	private int end_num = 0; 
	private static ArrayList<String> variable_list = new ArrayList<String>();
	
	
	public static void main(String[] args) {
		if (args.length == 0) { // Interactive system
			// TODO adding explicit parsing feature into Interactive system
			Scanner scanner = new Scanner(System.in);
			System.out.print(">> ");
			String input = scanner.nextLine();
			while (!input.equals("exit")) { 
				// TODO Parse the input string and write it in Java code then print the output.
				end_sign(input, true);
				System.out.print(">> ");
				input = scanner.nextLine();
			}
			System.out.println("Bye!");
			
		}else if (args.length == 1) { // Reading file from StdIn
			try {
				Scanner scanner = new Scanner(new File(args[0]));
				String output_filename = (args[0].split("\\.")[0]) + ".java";
				output_filename = output_filename.substring(0, 1).toUpperCase() + output_filename.substring(1);
				File output = new File(output_filename);
				if (output.createNewFile()) {
			        System.out.println("File created: " + output.getName());
			      } else {
			        System.out.println("File already exists.");
			      }
				FileWriter writer = new FileWriter(output);
				initializeOutFile(output_filename.split("\\.")[0], writer);while (scanner.hasNextLine()) {
					String cmd = scanner.nextLine();
					parseCmd(cmd, writer);
					// TODO Parse every line and translate to a Java code file.
				}
				writer.write("}}");
				writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else { // Accept command line arguments
			
		}
		
	}
	private static void initializeOutFile(String filename, FileWriter out) {
		String result = "public class " + filename + "{"
				+ "public static void main(String[] args) {";
		try {
			out.write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Private parsing method
	private static void parseCmd(String cmd, FileWriter out) {
		String line_result = "";
		String modified_cmd = cmd.trim();
		if (varAssign(modified_cmd, false)) {
			 String[] cur = modified_cmd.split(" ");
			 if (intVal.matcher(cur[2]).find()) {
				 line_result += "int " + modified_cmd;
			 } else if (strVal.matcher(cur[2]).find()) {
				 line_result += "String " + modified_cmd;
			 } else if (bool.matcher(cur[2]).find()) {
				 line_result += "boolean " + cur[0] + cur[1];
				 if (cur[2] == "TRUE") {
					 line_result += "true";
				 } else {
					 line_result += "false";
				 }
			 }
			line_result += ";";
		}else if (if_check(modified_cmd, false)) {
			
		}else if (else_check(modified_cmd, false)) {
			 
		}else if (loop(modified_cmd, false)) {
			
		}else if (end_sign(modified_cmd, false)){
			line_result += "};";
		}else if (print_val(modified_cmd, false)) {
			line_result += "System.out.println(";
			String print_val =
			modified_cmd.substring(modified_cmd.indexOf("(")+1,modified_cmd.indexOf(")"));		
			line_result += print_val + ");";
		}else if (print_var(modified_cmd, false)){
			line_result += "System.out.println(";
			String print_var = 
			modified_cmd.substring(6, modified_cmd.length()-1);
			line_result += print_var + ");";
		}
		else {
			System.out.println("Invalid code detected.");
			System.exit(0);
		}
		line_result += "\n";
		System.out.println(line_result);
		try {
			out.write(line_result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean varAssign(String cmd, boolean print) {
		Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			match = match && var(m.group(1), print);
			match = match && val(m.group(2), print) || expr(m.group(2), print);
			if (print)
				printMsg(match, "<varAssign>", cmd, "varAssign");
		}
		return match;
	}
	
	private static boolean print_var(String cmd, boolean print) {
		Matcher m = print_var.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			System.out.println(m.group(1));
			match = match && expr(m.group(1), print);
			if (var(m.group(1),print) && !(variable_list.contains(m.group(1)))) {
				System.out.println("COMPILE ERROR: That variable has not been declared.");
				System.exit(0);
			}
			else if (print)
				printMsg(match, "<print>", cmd, "print statement");
		}
		return match;
	}
	
	private static boolean print_val(String cmd, boolean print) {
		Matcher m = print_val.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			if (print)
				printMsg(match, "<print>", cmd, "print statement");
		}
		return match;
	}

	private static boolean if_check(String cmd, boolean print) {
		Matcher m = if_check.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			match = match && bool_expr(m.group(1), print);
			if (print)
				printMsg(match, "<if>", cmd, "if statement");
		}
		return match;
	}
	
	private static boolean else_check(String cmd, boolean print) {
		Matcher m = else_check.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			if (print)
				printMsg(match, "<else>", cmd, "else statement");
		}
		return match;
	}
	
	private static boolean loop(String cmd, boolean print) {
		Matcher m = loop.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			match = match && bool_expr(m.group(1), print); // <statement> check
			if (print)
				printMsg(match, "<loop>", cmd, "loop");
		}
		return match;
	}


	private static boolean bool_expr(String cmd, boolean print) { // TODO
		boolean match = bool_expr1(cmd, print);
		if(!match) {
			Matcher m = bool_expr.matcher(cmd);
			m.find();
			match = match && bool_expr(m.group(1),print);
			match = match && and_or(m.group(2),print);
			match = match && bool_expr1(m.group(3),print);
		}
		if (print)
			printMsg(match, "<bool_expr>", cmd, "boolean expression");
		return match;
	}

	private static boolean bool_expr1(String cmd, boolean print) { // TODO 
		Matcher m = comparative.matcher(cmd);
		boolean match = false;
		if(m.find() && print) {
			if (comparative(cmd, false) && print) {
				comparative(cmd, print);
			}
		}
		if (bool.matcher(cmd).find()) {
			match = true;
			if (bool(cmd, false) && print) {
				bool(cmd, print);
			}
		}
		if (var.matcher(cmd).find()){
			match = true;
			if (var(cmd, false) && print) {
				var(cmd, print);
			}
		}
		return match;
	}
	
	private static boolean expr(String cmd, boolean print) {
		boolean match = val(cmd, print);
		if(!match) {
			if (!(cmd.charAt(0) == '(' )|| !(cmd.charAt(cmd.length()-1) == ')')) {
				System.out.println("SYNTAX ERROR: Expression should have parentheses.");
				System.exit(0);
			}
			Matcher m = expr.matcher(cmd);
			match = m.find();
			match = match && expr(m.group(1),print);
			match = match && op(m.group(2),print);
			match = match && val(m.group(3),print);
		}
		if (print)
			printMsg(match, "<expr>", cmd, "expression");
		return match;
	}
	
	private static boolean comparator(String cmd, boolean print) {
		Matcher m = comparator.matcher(cmd);
		boolean match = false;
		match = m.find();
		if (print)
			printMsg(match, "<comparator>", cmd, "comparator");
		return match;
	}
	
	private static boolean comparative(String cmd, boolean print) {
		Matcher m = comparative.matcher(cmd);
		boolean match = false;
		if (m.find()) {
			match = true;
			match = match && expr(m.group(1), print);
			match = match && comparator(m.group(2),print);
			match = match && expr(m.group(3), print);
			if (print)
				printMsg(match, "<comparative>", cmd, "comparative");
		}
		return match;
	}
	
	private static boolean and_or(String cmd, boolean print) {
		Matcher m = and_or.matcher(cmd);
		boolean match = false;
		match = m.find();
		if (print)
			printMsg(match, "<and_or>", cmd, "And|Or");
		return match;
	}
	
	private static boolean end_sign(String cmd, boolean print) {
		Matcher m = end_sign.matcher(cmd);
		boolean match = m.find();
		if (print) 
			printMsg(match, "<end_sign>", cmd, "End sign");
		return match;
	}
	
	private static boolean var(String cmd, boolean print) {
		Matcher m = var.matcher(cmd);
		boolean match = m.find();
		if (match) variable_list.add(cmd);
		if (print) 
			printMsg(match, "<var>", cmd, "variable");
		return match;
	}
	
	private static boolean val(String cmd, boolean print) {
		Matcher m = intVal.matcher(cmd);
		boolean match = m.find();
		if(match && print) {
			printMsg(match, "<int>", cmd, "integer");
		}else if (strVal.matcher(cmd).find()) {
			match = true;
			if (print)
				printMsg(match, "<String>", cmd, "string");
		}else if (bool.matcher(cmd).find()){
			match = true;
			if (print)
				printMsg(match, "<bool>", cmd, "boolean");
		}else {
			m = var.matcher(cmd);
			match = m.find();
			if(match && print)
				printMsg(match, "<var>", cmd, "variable");
		}
		return match;
	}
	
	private static boolean op(String cmd, boolean print) {
		Matcher m = op.matcher(cmd);
		boolean match = false;
		match = m.find();
		if (print)
			printMsg(match, "<op>", cmd, "operator");
		return match;
	}
	
	private static boolean bool(String cmd, boolean print) {
		Matcher m = bool.matcher(cmd);
		if (cmd == "true" || cmd == "false") {
			System.out.println("SYNTAX ERROR: Booleans should be capitalized.");
			System.exit(0);
		}
		boolean match = false;
		match = m.find();
		if (print)
			printMsg(match, "<bool>", cmd, "boolean");
		return match;
	}
		
	private static void printMsg(boolean match, String ntName, String cmd, String item) {
		if(match)
			System.out.println(ntName + ": " + cmd);
		else
			System.out.println("Failed to parse: {" + cmd + "} is not a valid " + item + ".");
	}

}