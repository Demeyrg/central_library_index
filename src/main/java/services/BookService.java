package services;

import entity.Book;

import java.util.Map;

public interface BookService {
    Map<Long, Book> findBook(String[] params);

    void orderBook(String[] params);

    void returnBook(String id);
}
