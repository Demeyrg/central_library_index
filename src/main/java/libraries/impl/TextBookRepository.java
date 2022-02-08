package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.BookMapper;
import mapper.impl.TextBookMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextBookRepository implements BookRepository {

    private BookMapper bookMapper = new TextBookMapper();

    public static final File PATH = new File("src/main/resources/libraries/");

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        File[] files = PATH.listFiles();
        for (File file: files) {
            if (!file.getName().startsWith("Text_") || !file.isDirectory())
                continue;
            takeBookFromLibrary(file.listFiles(), books);
        }
        return books;
    }

    private void takeBookFromLibrary(File[] files, List<Book> books) {
        for(File file: files) {
            Book book = bookMapper.createBook(file);
            books.add(book);
        }
    }



    @Override
    public void orderBook(Book book) {

    }

    @Override
    public void returnBook(Book book) {

    }
}
