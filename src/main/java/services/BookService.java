package services;

import entity.Book;
import libraries.BookRepository;
import libraries.impl.CSVBookRepository;
import libraries.impl.TextBookRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {

    private BookRepository bookRepository;

    private static Map<Long, Book> allBooks = new HashMap<>();

    public Map<Long, Book> findBook(String[] line) {
        allBooks.clear();
        List<Book> books = getAllBooksInRepositories();
        if (line.length == 2)
            searchBookByOneParam(line[1], books);
        if (line.length == 3)
            searchBookByTwoParam(line[1], line[2], books);
        return BookService.allBooks;
    }

    private List<Book> getAllBooksInRepositories() {
        List<Book> books = new ArrayList<>();
        bookRepository = new CSVBookRepository();
        books.addAll(bookRepository.getAllBooks());
        bookRepository = new TextBookRepository();
        books.addAll(bookRepository.getAllBooks());
        return books;
    }

    private void searchBookByOneParam(String param, List<Book> books) {
        String[] line = param.split("=");
        if (line.length != 2) {
            printMessageInvalidFindError();
            return;
        }
        String searchProperty = line[0];
        String searchElement = line[1];

        for (Book book : books) {
            String bookElement;
            if (searchProperty.equalsIgnoreCase("author")) {
                bookElement = book.getAuthor();
            } else if (searchProperty.equalsIgnoreCase("name")) {
                bookElement = book.getName();
            } else {
                printMessageInvalidFindError();
                break;
            }

            if (bookElement.equals(searchElement) || bookElement.startsWith(searchElement)
                    || bookElement.endsWith(searchElement))
                allBooks.put(book.getId(),book);
        }
    }


    private void searchBookByTwoParam(String paramOne, String paramTwo, List<Book> books) {
        String[] lineOne = paramOne.split("=");
        String[] lineTwo = paramTwo.split("=");
        if (lineOne.length != 2 || lineTwo.length != 2) {
            printMessageInvalidFindError();
            return;
        }
        for (Book book: books) {
            if (lineOne[0].equalsIgnoreCase("author") && lineTwo[0].equalsIgnoreCase("name"))
                searchByAuthorAndName(lineOne[1],lineTwo[1], book);
            else if (lineTwo[0].equalsIgnoreCase("author") && lineOne[0].equalsIgnoreCase("name"))
                searchByAuthorAndName(lineTwo[1],lineOne[1], book);
            else
                printMessageInvalidFindError();
        }
    }

    private void searchByAuthorAndName(String author, String name, Book book) {
        String authorBook = book.getAuthor();
        String nameBook = book.getName();
        if ( (authorBook.equals(author) || authorBook.startsWith(author) || authorBook.endsWith(author))
                && (nameBook.equals(name) || nameBook.startsWith(name) || nameBook.endsWith(name)) )
            allBooks.put(book.getId(), book);
    }

    private void printMessageInvalidFindError() {
        System.out.println("Формат ввода: FIND [author=<автор>] [name=<bookname>] (можно с одним параметром)");
    }

    public void orderBook(String[] params) {
        String paramId = params[1];
        String paramIssuedTo = params[2];
        if (!isParam(paramId) || !isParam(paramIssuedTo)) {
            System.out.println("Формат ввода: ORDER id=<индекс> abonent=<имя абонента>");
            return;
        }

        Long id = 0l;
        try {
            id = Long.valueOf(paramId.split("=")[1]);
        } catch (NumberFormatException e) {
            System.out.println("Id должно быть числом");
        }
        String issuedTo = paramIssuedTo.split("=")[1];
//        orderBookInRepository(new TextBookRepository(), id,issuedTo);
        orderBookInRepository(new CSVBookRepository(), id,issuedTo);
    }

    private void orderBookInRepository(BookRepository repository, Long id, String issuedTo) {
        Book book = repository.orderBook(id, issuedTo);
        if (book != null && issuedTo.equals(book.getIssuedTo()))
            System.out.printf("OK abonent=%s date=%s", book.getIssuedTo(), book.getIssued());
        else if (book != null && !issuedTo.equals(book.getIssuedTo()) && book.getId() != null)
            System.out.printf("RESERVED abonent=%s date=%s", book.getIssuedTo(), book.getIssued());
        else
            System.out.println("NOTFOUND");
    }

    private boolean isParam(String param) {
        String[] splitParam = param.split("=");
        if (splitParam.length == 2) {
            return true;
        }
        return false;
    }
}
