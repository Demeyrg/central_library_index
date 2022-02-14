package handlers;


import libraries.impl.CSVBookRepository;
import libraries.impl.TextBookRepository;
import services.BookServiceImpl;

import java.io.File;
import java.util.Arrays;

public class CommandHandler {

    private final File[] libraries = new File("src/main/resources/libraries/").listFiles();

    private static CommandHandler commandHandler;
    private final BookServiceImpl bookService;

    {
        bookService = new BookServiceImpl(Arrays.asList(
                new TextBookRepository(libraries),
                new CSVBookRepository(libraries)
        ));
    }

    private CommandHandler() {
    }


    public static CommandHandler getInstance() {
        if(commandHandler == null)
            commandHandler = new CommandHandler();
        return commandHandler;
    }

    public void handling(String command) {
        String[] line = command.split(" ");
        switch (line[0]) {
            case "FIND":
                findBooks(line);
                break;
            case "ORDER":
                orderBook(line);
                break;
            case "RETURN":
                returnBook(line);
                break;
            default:
                System.out.println("SYNTAXERROR");
        }
    }

    private void findBooks(String[] params) {
        if (params.length == 2 && isParam(params[1])
                && ( params[1].startsWith("name=") || params[1].startsWith("author=") ) )
            bookService.findBookByOneParam(params[1]);
        else if (params.length == 3 && isParam(params[1]) && isParam(params[2])
                && ( params[1].startsWith("name=") || params[1].startsWith("author=") )
                && ( params[2].startsWith("name=") || params[2].startsWith("author=") ) )
            bookService.findBookByTwoParam(params[1], params[2]);
        else
            System.out.println("SYNTAXERROR");
    }

    private void orderBook(String[] params) {
        if (params.length != 3 || !params[1].startsWith("id=") || !params[2].startsWith("abonent=")
                || !isParam(params[1]) || !isParam(params[2])) {
            System.out.println("Формат ввода: ORDER id=<индекс> abonent=<имя абонента>");
            return;
        }
        bookService.orderBook(params);
    }

    private void returnBook(String[] params) {
        if (params.length != 2 || !params[1].startsWith("id=") || !isParam(params[1])) {
            System.out.println("Формат ввода: RETURN id=<индекс>");
            return;
        }
        bookService.returnBook(params[1]);
    }

    private boolean isParam(String param) {
        String[] splitParam = param.split("=");
        return splitParam.length == 2;
    }
}