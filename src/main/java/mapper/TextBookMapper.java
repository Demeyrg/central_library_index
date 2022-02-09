package mapper;

import entity.Book;

import java.io.*;

public class TextBookMapper {

    public Book createBook(File file) {
        Book book = new Book();
        book.setLibrary(file.getParentFile().getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String[] splitLine = reader.readLine().split("=");
                if (splitLine.length < 2)
                    continue;
                fillBook(splitLine[0], splitLine[1], book);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }


    private void fillBook(String firstElement, String secondElement, Book book) {

        if (firstElement.equalsIgnoreCase("Index"))
            book.setId(Long.valueOf(secondElement));

        if (firstElement.equalsIgnoreCase("Author"))
            book.setAuthor(secondElement);

        if (firstElement.equalsIgnoreCase("Name"))
            book.setName(secondElement);

        if (firstElement.equalsIgnoreCase("Issued"))
            book.setIssued(secondElement);

        if (firstElement.equalsIgnoreCase("IssuedTo"))
            book.setIssuedTo(secondElement);

    }
}
