package libraries;

import entity.Book;

import java.util.List;

public interface BookRepository {
    List<Book> getAllBooks();

    void returnBook(Book book);

    Book orderBook(Long id, String issuedTo);
}
