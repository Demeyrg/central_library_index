package libraries;

import entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> returnBookInLibrary(Long id);

    Optional<Book> orderBookInLibrary(Long id, String issued, String issuedTo);

    List<Book> getBooksByName(String name);

    List<Book> getBooksByAuthor(String author);

    List<Book> getBooksByAuthorAndName(String author, String name);
}