package libraries.impl;

import entity.Book;
import libraries.BookRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVBookRepository implements BookRepository {
    @Override
    public List<Book> getAllBooks() {
        Map<Long,Book> books = new HashMap<>();
        return null;
    }

    @Override
    public void orderBook(Book book) {

    }

    @Override
    public void returnBook(Book book) {

    }
}
