package libraries;

import entity.Book;

import java.util.List;
import java.util.Map;

public interface BookRepository {
    List<Book> getAllBooks();

    void orderBook(Book book);

    void returnBook(Book book);
}
