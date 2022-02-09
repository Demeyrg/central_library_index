package mapper;

import entity.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVBookMapper {

    public List<Book> createListBooks(File file) {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String[] splitLine = reader.readLine().split(",");
                Book book = new Book();
                book.setLibrary(file.getParentFile().getName());
                createBook(splitLine, book);
                books.add(book);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void createBook(String[] splitLine, Book book) {

        if (splitLine[0] != null)
            book.setId(Long.valueOf(splitLine[0]));

        if (splitLine[1] != null)
            book.setAuthor(splitLine[1]);

        if (splitLine[2] != null)
            book.setName(splitLine[2]);

        if (splitLine.length > 3 && splitLine[3] != null && !splitLine[3].equals(""))
            book.setIssued(splitLine[3]);

        if (splitLine.length > 4 && splitLine[4] != null && !splitLine[4].equals(""))
            book.setIssuedTo(splitLine[4]);
    }

}
