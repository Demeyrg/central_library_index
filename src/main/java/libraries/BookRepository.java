package libraries;

import entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> getAllBooks();

    Optional<Book> returnBook(Long id);

    Book orderBook(Long id, String issuedTo);
}
