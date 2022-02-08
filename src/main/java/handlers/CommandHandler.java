package handlers;


import entity.Book;
import services.BookService;

import java.util.List;

public class CommandHandler {

    public static CommandHandler commandHandler;

    private BookService bookService = new BookService();

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
                orderBook();
                break;
            case "RETURN":
                returnBook();
                break;
        }
    }

    private void findBooks(String[] line) {
        bookService.findBook(line);
    }

    private void orderBook() {
        System.out.println("orderBook");
    }

    private void returnBook() {
        System.out.println("returnBook");
    }
}
