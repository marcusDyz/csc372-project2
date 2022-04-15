import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
	private static Pattern bool_expr = Pattern.compile("^\\((.+) [\\^#] (.+)\\)$");
	private static Pattern expr = Pattern.compile("^\\((.+) (.) (.+)\\)$");
	private static Pattern end_sign = Pattern.compile("^\\]$");
	private static Pattern intVal = Pattern.compile("^\\d+$");
	private static Pattern strVal = Pattern.compile("^\"\\w+\"$");
	private static Pattern and_or = Pattern.compile("^#|\\^$");
	private static Pattern var = Pattern.compile("^\\w+$");
	private static Pattern op = Pattern.compile("^[+-/%]{1}|\\*$");
	private static Pattern bool = Pattern.compile("^TRUE|FALSE$");
	private int end_num = 0; 
	private static HashMap<String, String> variable_list = new HashMap<String, String>();
	
	
	public static void main(String[] args) {
		if (args.length == 0) { // Interactive system
			// TODO adding explicit parsing feature into Interactive system
			Scanner scanner = new Scanner(System.in);
			System.out.print(">> ");
			String input = scanner.nextLine();
			while (!input.equals("exit")) { 
				// TODO Parse the input string and write it in Java code then print the output.
				varAssign(input, true);
				System.out.print(">> ");
				input = scanner.nextLine();
			}
			System.out.println("Bye!");
			
		}else { // Reading file from StdIn
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
				initializeOutFile(output_filename.split("\\.")[0], writer);
				if (args.length > 1) {
					for (int i=1; i<args.length; i++) {
						String cla = "CLA" + (i) + " = " + args[i];
						System.out.println(cla);
						parseCmd(cla,writer);
					}
				}
				while (scanner.hasNextLine()) {
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
			 String[] list_for_expr = modified_cmd.split("=");
			 if (!variable_list.containsKey(cur[0])) {
				 if (intVal.matcher(cur[2]).find()) {
					 line_result += "Integer " + modified_cmd;
					 variable_list.put(cur[0], "Integer");
				 }else if (strVal.matcher(cur[2]).find()) {
					 line_result += "String " + modified_cmd;
					 variable_list.put(cur[0], "String");
				 }else if (bool.matcher(cur[2]).find()) {
					 line_result += "Boolean " + cur[0] + cur[1];
					 if (cur[2].equals("TRUE")) {
						 line_result += "true";
					 }else {
						 line_result += "false";
					 }
					 variable_list.put(cur[0], "String");
				 }else if (expr.matcher(list_for_expr[1].trim()).find()) {
					 line_result += "Integer " + modified_cmd;
					 variable_list.put(cur[0], "Integer");
				 }else if (var.matcher(cur[2]).find()) {
					 if (!variable_list.containsKey(cur[2])) {
						 System.out.println("Undeclared variable!");
						 System.exit(0);
					 }else {
						 line_result += variable_list.get(cur[2]) + " " + modified_cmd;
						 variable_list.put(cur[0], variable_list.get(cur[2]));
					 }
				 }	 
			 }else {
				 if (intVal.matcher(cur[2]).find() || strVal.matcher(cur[2]).find()) {
					 line_result += modified_cmd;
				 }else if (bool.matcher(cur[2]).find()) {
					 line_result += cur[0] + cur[1];
					 if (cur[2].equals("TRUE")) {
						 line_result += "true";
					 }else {
						 line_result += "false";
					 }
				 }else if (expr.matcher(list_for_expr[1].trim()).find()) {
					 line_result += modified_cmd;
				 }else if (var.matcher(cur[2]).find()) {
					 if (!variable_list.containsKey(cur[2])) {
						 System.out.println("Undeclared variable!");
						 System.exit(0);
					 }else {
						 line_result += modified_cmd;
					 }
				 }
			 }
			 line_result += ";";
		}else if (if_check(modified_cmd, false)) {
			String[] cur = modified_cmd.split(" ");
			String expression = "";
			for (int i=1; i< cur.length-1; i++) {
				if (i == cur.length-2)
					expression += cur[i];
				else
					expression += cur[i] + " ";
			}
			System.out.println(expression);
			if (comparative(expression.substring(1, expression.length()-1), false)) {
				line_result += modified_cmd.split("\\[")[0] + "{";
			}else {
				line_result += "if (" + 
			translate_bool_expr(modified_cmd.substring(3, modified_cmd.length()-1)) + "{";
			}
		}else if (else_check(modified_cmd, false)) {
			line_result += modified_cmd.split("\\[")[0] + "{";
		}else if (loop(modified_cmd, false)) {
			String[] cur = modified_cmd.split(" ");
			String expression = "";
			for (int i=1; i< cur.length-1; i++) {
				if (i == cur.length-2)
					expression += cur[i];
				else
					expression += cur[i] + " ";
			}
			if (comparative(expression.substring(1, expression.length()-1), false)) {
				line_result += modified_cmd.split("\\[")[0] + "{";
			}else {
				line_result += "while (" + 
					translate_bool_expr(modified_cmd.substring(7, modified_cmd.length()-1)) + "{";
			}
		}else if (end_sign(modified_cmd, false)){
			line_result += "}";
		}else if (print_val(modified_cmd, false)) {
			line_result += "System.out.print(";
			String print_val =
			modified_cmd.substring(modified_cmd.indexOf("(")+1,modified_cmd.indexOf(")"));
			if (bool.matcher(print_val).find()) {
				line_result += print_val.toLowerCase() + ");";
			}else {
				line_result += print_val + ");";
			}
		}else if (print_var(modified_cmd, false)){
			line_result += "System.out.print(";
			String print_var = 
			modified_cmd.substring(modified_cmd.indexOf("(")+1,modified_cmd.indexOf(")"));
			if (bool.matcher(print_var).find()) {
				line_result += print_var.toLowerCase() + ");";
			}else {
				line_result += print_var + ");";
			}
		}
		else {
			System.out.println("Invalid code detected.");
			System.exit(0);
		}
		System.out.println(line_result); // DEBUG
		try {
			out.write(line_result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String translate_bool_expr(String str) {
		String result = "";
		String[] substr = str.split("");
		for (String s : substr) {
			if (s.equals("^")) {
				result += "&&";
			}else if (s.equals("#")) {
				result += "||";
			}else if ("TRUE".contains(s) || "FALSE".contains(s)) {
				result += s.toLowerCase();
			}else {
				result += s;
			}
			//System.out.println("Check: " + result);
		}
		return result;
	}
	
	private static boolean varAssign(String cmd, boolean print) {
		Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			match = match && var(m.group(1), print);
			match = match && (val(m.group(2), print) || expr(m.group(2), print));
			if (print)
				printMsg(match, "<varAssign>", cmd, "varAssign");
		}
		return match;
	}
	
	public static boolean print_var(String cmd, boolean print) {
		Matcher m = print_var.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			if (print)
				printMsg(match, "<print>", cmd, "print statement");
		}
		return match;
	}
	
	public static boolean print_val(String cmd, boolean print) {
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
		Matcher m = bool_expr.matcher(cmd);
		boolean match = m.find();
		match = match && bool_expr(m.group(1),print);
		match = match && bool_expr1(m.group(2),print);
		if (!match) {
			match = bool_expr1(cmd, false);
		}
		if (print)
			printMsg(match, "<bool_expr>", cmd, "boolean expression");
		return match;
	}

	private static boolean bool_expr1(String cmd, boolean print) { // TODO 
		Matcher m = comparative.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
			if (comparative(cmd, false) && print) {
				comparative(cmd, print);
			}
		}else if (bool.matcher(cmd).find()) {
			match = true;
			if (bool(cmd, false) && print) {
				bool(cmd, print);
			}
		}else if (var.matcher(cmd).find()){
			match = true;
			if (var(cmd, false) && print) {
				var(cmd, print);
			}
		}
		if (print)
			printMsg(match, "<bool_expr1>", cmd, "boolean expression 1");
		return match;
	}
	
	private static boolean expr(String cmd, boolean print) {
		Matcher m = expr.matcher(cmd);
		boolean match = m.find();
		if (match) {
			match = match && expr(m.group(1),print);
			match = match && op(m.group(2),print);
			match = match && val(m.group(3),print);
		}else 
			match = val(cmd, print);
		if (print)
			printMsg(match, "<expr>", cmd, "expr");
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