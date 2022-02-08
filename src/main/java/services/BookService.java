package services;

import entity.Book;
import libraries.impl.CSVBookRepository;
import libraries.impl.TextBookRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {

    private CSVBookRepository CSVBooksRepo = new CSVBookRepository();
    private TextBookRepository textBooksRepo = new TextBookRepository();

    private static Map<Long, Book> allBooks = new HashMap<>();

    public void findBook(String[] line) {
//        if (allBooks.size() != 0)

        List<Book> textBooks = textBooksRepo.getAllBooks();
//      List<Book> CSVBooks = csvBooksRepo.getAllBooks();
        addBooks(textBooks);
//        addBooks(CSVBooks);
        if (line.length == 2)
            searchBook(line[1]);
        if (line.length == 3)
            searchBook(line[1], line[2]);
    }

    private void searchBook(String param) {
        String[] line = param.split("=");
        if (line[0].equalsIgnoreCase("author"))
            searchByAuthor(line[1]);
        else if (line[0].equalsIgnoreCase("name"))
            searchByName(line[1]);
        else
            System.out.println("Формат ввода: FIND [author=<автор>] [name=<bookname>]");
    }

    private void searchBook(String paramOne, String paramTwo) {
        for (Map.Entry<Long,Book> book: allBooks.entrySet()) {

        }
    }

    private void searchByAuthor(String author) {
        for (Map.Entry<Long,Book> book: allBooks.entrySet()) {
            String authorBook = book.getValue().getAuthor();
            if (authorBook.equals(author) || authorBook.startsWith(author) || authorBook.endsWith(author))
                System.out.printf("FOUND id=%d, lib=%s\n", book.getKey(),book.getValue().getLibrary());
        }
    }

    private void searchByName(String name) {
        for (Map.Entry<Long, Book> book : allBooks.entrySet()) {
            String nameBook = book.getValue().getName();
            if (nameBook.equals(name) || nameBook.startsWith(name)
                    || nameBook.endsWith(name) && book.getValue().getIssuedTo().equals("")) {
                System.out.printf("FOUND id=%d, lib=%s\n"
                        , book.getKey()
                        , book.getValue().getLibrary());
            } else if (!book.getValue().getIssued().equals("")) {
                System.out.printf("FOUNDMISSING id=%d, lib=%s, issued=%s\n"
                        , book.getKey()
                        , book.getValue().getLibrary()
                        , book.getValue().getIssued());
            } else {
                System.out.println();
            }
        }
    }

    private void addBooks(List<Book> books) {
        for (Book book: books)
            BookService.allBooks.put(book.getId(), book);
    }
}
