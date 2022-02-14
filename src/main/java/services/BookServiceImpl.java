package services;

import entity.Book;
import libraries.BookRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService{

    private final List<BookRepository> bookRepositories;

    public BookServiceImpl(List<BookRepository> bookRepositories) {
        this.bookRepositories = bookRepositories;
    }

    @Override
    public void findBookByOneParam(String param) {
        List<Book> books = new ArrayList<>();
        String[] paramAndValue = param.split("=");
        for (BookRepository bookRepository : bookRepositories) {
            if (paramAndValue[0].equals("author"))
                books.addAll(bookRepository.getBooksByAuthor(paramAndValue[1]));
            else
                books.addAll(bookRepository.getBooksByName(paramAndValue[1]));
        }
        printFindResult(books);
    }

    @Override
    public void findBookByTwoParam(String paramOne, String paramTwo) {
        List<Book> books = new ArrayList<>();
        String[] paramAndValueFirst = paramOne.split("=");
        String[] paramAndValueSecond = paramTwo.split("=");
        for (BookRepository bookRepository : bookRepositories) {
            if (paramAndValueFirst[0].equals("author") && paramAndValueSecond[0].equals("name"))
                books.addAll(bookRepository.getBooksByAuthorAndName(paramAndValueFirst[1],paramAndValueSecond[1]));
            else
                books.addAll(bookRepository.getBooksByAuthorAndName(paramAndValueSecond[1],paramAndValueFirst[1]));
        }
        printFindResult(books);
    }

    private void printFindResult(List<Book> books) {
        if (books.size() == 0) {
            System.out.println("NOTFOUND");
            return;
        }
        for (Book book: books) {
            if (book.getIssuedTo().equals(""))
                System.out.printf("FOUND id=%d lib=%s\n", book.getId(),book.getLibrary());
            else
                System.out.printf("FOUNDMISSING id=%d lib=%s issued=%s\n",book.getId(),book.getLibrary(),book.getIssued());
        }
    }

    @Override
    public void orderBook(String[] params) {
        String paramId = params[1];
        String paramIssuedTo = params[2];
        Long id = convertToNumber(paramId);
        if (id == null)
            return;
        String issuedTo = paramIssuedTo.split("=")[1];

        orderBookInRepository(id,issuedTo);
    }

    private void orderBookInRepository(Long id, String issuedTo) {
        Optional<Book> optionalBook = Optional.empty();
        String issued = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        for (BookRepository repository: bookRepositories) {
            optionalBook = repository.orderBookInLibrary(id, issued, issuedTo);
            if(optionalBook.isEmpty()) {
                continue;
            }
            Book book = optionalBook.get();
            if (book.getIssuedTo().equals("")) {
                System.out.printf("OK abonent=%s date=%s",issuedTo,issued);
            } else {
                System.out.printf("RESERVED abonent=%s date=%s", book.getIssuedTo(),book.getIssued());
            }
            break;
        }
        if (optionalBook.isEmpty())
            System.out.println("NOTFOUND");
    }

    @Override
    public void returnBook(String idStr) {
        Long id = convertToNumber(idStr);
        if (id == null)
            return;
        returnBookInRepository(id);
    }

    private void returnBookInRepository(Long id) {
        Optional<Book> optionalBook = Optional.empty();
        for (BookRepository repository: bookRepositories) {
            optionalBook = repository.returnBookInLibrary(id);
            if(optionalBook.isEmpty()) {
                continue;
            }
            Book book = optionalBook.get();
            if (book.getIssuedTo().equals("")) {
                System.out.println("ALREADYRETURNED");
            } else {
                System.out.println("OK abonent=" + book.getIssuedTo());
            }
            break;
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
