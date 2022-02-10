package handlers;


import entity.Book;
import services.BookServiceImpl;

import java.util.List;
import java.util.Map;

public class CommandHandler {

    public static CommandHandler commandHandler;

    private BookServiceImpl bookService = new BookServiceImpl();

    private CommandHandler() {
    }


    public static CommandHandler getInstance() {
        if(commandHandler == null)
            commandHandler = new CommandHandler();
        return commandHandler;
    }

    public void handling(String command) {
        String[] line = command.split(" ");
        List<Book> books;
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

    private void findBooks(String[] line) {
        Map<Long, Book> books = bookService.findBook(line);
        if (books.size() == 0) {
            System.out.println("NOTFOUND");
            return;
        }

        for (Map.Entry<Long, Book> book : books.entrySet()) {
            if (book.getValue().getIssuedTo().equals(""))
                System.out.printf("FOUND id=%d, lib=%s\n"
                        , book.getKey()
                        , book.getValue().getLibrary());
            else
                System.out.printf("FOUNDMISSING id=%d, lib=%s, issued=%s\n"
                        , book.getKey()
                        , book.getValue().getLibrary()
                        , book.getValue().getIssued());
        }
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
        if (splitParam.length == 2) {
            return true;
        }
        return false;
    }
}
