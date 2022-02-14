package mapper;

import entity.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVBookMapper {

    public List<Book> returnListBooks(File file) {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String[] splitLine = reader.readLine().split(",");
                books.add(returnBook(splitLine, file.getParentFile().getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    private Book returnBook(String[] splitLine, String name) {
        Book book = new Book();
        book.setLibrary(name);

        if (splitLine[0] != null)
            book.setId(Long.valueOf(splitLine[0]));

        if (splitLine[1] != null)
            book.setAuthor(splitLine[1]);

        if (splitLine[2] != null)
            book.setName(splitLine[2]);

        if (splitLine.length > 3 && splitLine[3] != null)
            book.setIssued(splitLine[3]);

        if (splitLine.length > 4 && splitLine[4] != null)
            book.setIssuedTo(splitLine[4]);
        return book;
    }
}
