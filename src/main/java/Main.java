import handlers.CommandHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String str = reader.readLine();
                if (str.equalsIgnoreCase("EXIT"))
                    break;
                commandExecution(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commandExecution(String str) {
        if(!isCommand(str))
            return;
        CommandHandler commandHandler = CommandHandler.getInstance();
        commandHandler.handling(str);
    }


    private static boolean isCommand(String command) {
        String[] line = command.split(" ");
        if (line.length <=1 || line.length > 3){
            System.out.println("SYNTAXERROR");
            return false;
        }
        return true;
    }
}
