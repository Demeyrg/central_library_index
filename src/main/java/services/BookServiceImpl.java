package services;

import entity.Book;
import libraries.BookRepository;
import libraries.impl.CSVBookRepository;
import libraries.impl.TextBookRepository;

import java.util.*;

public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;

    private static Map<Long, Book> allBooks = new HashMap<>();

    @Override
    public Map<Long, Book> findBook(String[] line) {
        allBooks.clear();
        List<Book> books = getAllBooksInRepositories();
        if (line.length == 2)
            searchBookByOneParam(line[1], books);
        if (line.length == 3)
            searchBookByTwoParam(line[1], line[2], books);
        return BookServiceImpl.allBooks;
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

    @Override
    public void orderBook(String[] params) {
        String paramId = params[1];
        String paramIssuedTo = params[2];
        Long id = convertToNumber(paramId);
        if (id == null)
            return;
        String issuedTo = paramIssuedTo.split("=")[1];
        orderBookInRepository(id,issuedTo, new CSVBookRepository(), new TextBookRepository());
    }

    private void orderBookInRepository(Long id, String issuedTo,BookRepository ... repositories) {
        System.out.println("ПРивет, я сломал этот метод");
//        Book book = null;
//        for (BookRepository repository: repositories) {
//            book = repository.orderBook(id, issuedTo);
//            if (book != null && book.getIssuedTo().equals(issuedTo)) {
//                System.out.printf("OK abonent=%s date=%s", book.getIssuedTo(), book.getIssued());
//                break;
//            }
//            else if (book != null && !issuedTo.equals(book.getIssuedTo()) && book.getId() != null) {
//                System.out.printf("RESERVED abonent=%s date=%s", book.getIssuedTo(), book.getIssued());
//                break;
//            }
//        }
//        if (book == null)
//            System.out.println("NOTFOUND");
    }

    @Override
    public void returnBook(String idStr) {
        Long id = convertToNumber(idStr);
        if (id == null)
            return;
        returnBookInRepository(id, new CSVBookRepository(),new TextBookRepository());
    }

    private void returnBookInRepository(Long id, BookRepository ... repositories) {
        Optional<Book> optionalBook = Optional.empty();
        for (BookRepository repository: repositories) {
            optionalBook = repository.returnBook(id);
            if(!optionalBook.isPresent()) {
                continue;
            }
            Book book = optionalBook.get();
            if (book.getIssuedTo().equals("")) {
                System.out.println("ALREADYRETURNED");
                break;
            } else {
                System.out.println("OK abonent=" + book.getIssuedTo());
                break;
            }
        }
        if (optionalBook.isEmpty())
            System.out.println("NOTFOUND");
    }

    private Long convertToNumber(String paramId) {
        Long id;
        try {
            id = Long.valueOf(paramId.split("=")[1]);
        } catch (NumberFormatException e) {
            id = null;
            System.out.println("Id должно быть числом");
        }
        return id;
    }
}
