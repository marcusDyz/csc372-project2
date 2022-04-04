import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Translator {
	
	public static void main(String[] args) {
		if (args.length == 0) { // Interactive system
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			while (!input.equals("exit")) { 
				// TODO Parse the input string and write it in Java code then print the output.
				input = scanner.nextLine();
			}
			System.out.println("Bye!");
			
		}else if (args.length == 1) { // Reading file from StdIn
			try {
				Scanner scanner = new Scanner(new File(args[0]));
				while (scanner.hasNextLine()) {
					// TODO Parse every line and translate to a Java code file.
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Cannot find the file!");
			}
		}else { // Error handling
			System.out.println("Has to be zero or one command line argument!");
		}
		
	}
	
	// Private parsing method

}
