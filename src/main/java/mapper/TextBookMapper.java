package mapper;

import entity.Book;

import java.io.*;

public class TextBookMapper {

    public Book returnBook(File file) {
        Book book = new Book();
        book.setLibrary(file.getParentFile().getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String[] splitLine = reader.readLine().split("=");
                if (splitLine.length < 2)
                    continue;
                fillBook(splitLine[0], splitLine[1], book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }


    public void fillBook(String param, String value, Book book) {

        if (param.equalsIgnoreCase("Index"))
            book.setId(Long.valueOf(value));

        if (param.equalsIgnoreCase("Author"))
            book.setAuthor(value);

        if (param.equalsIgnoreCase("Name"))
            book.setName(value);

        if (param.equalsIgnoreCase("Issued"))
            book.setIssued(value);

        if (param.equalsIgnoreCase("IssuedTo"))
            book.setIssuedTo(value);

    }

}
