package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.CSVBookMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CSVBookRepository implements BookRepository {

    private final CSVBookMapper CSVbookMapper = new CSVBookMapper();
    private final File[] files;

    public CSVBookRepository(File[] files) {
        this.files = files;
    }

    @Override
    public List<Book> getBooksByName(String name) {
        List<Book> allBooks = getAllBooksFromLibrariesCSV();
        List<Book> booksByParam = new ArrayList<>();
        for (Book book:allBooks) {
            if (book.getName().contains(name))
                booksByParam.add(book);
        }
        return booksByParam;
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        List<Book> allBooks = getAllBooksFromLibrariesCSV();
        List<Book> booksByParam = new ArrayList<>();
        for (Book book:allBooks) {
            if (book.getAuthor().contains(author))
                booksByParam.add(book);
        }
        return booksByParam;
    }

    @Override
    public List<Book> getBooksByAuthorAndName(String author, String name) {
        List<Book> allBooks = getAllBooksFromLibrariesCSV();
        List<Book> booksByParams = new ArrayList<>();
        for (Book book:allBooks) {
            if (book.getName().contains(name) && book.getAuthor().contains(author))
                booksByParams.add(book);
        }
        return booksByParams;
    }

    private List<Book> getAllBooksFromLibrariesCSV() {
        List<Book> books = new ArrayList<>();
        assert files != null;
        for (File libraries: files) {
            if (!libraries.getName().startsWith("CSV_") || !libraries.isDirectory()
                    || Objects.requireNonNull(libraries.listFiles()).length <= 0)
                continue;
            for (File file : Objects.requireNonNull(libraries.listFiles())) {
                if (file.isDirectory())
                    continue;
                books.addAll(CSVbookMapper.returnListBooks(file));
            }
        }
        return books;
    }

    @Override
    public Optional<Book> orderBook(Long id, String issued, String issuedTo) {
        List<File> filesInLibraries = getFilesInLibraries();
        Book book = new Book();
        for (File file: filesInLibraries) {
            StringBuilder builder = new StringBuilder();
            book = createBookByIdIfExist(id,issued,issuedTo,builder,file);
            if (book.getId() != null && book.getId().equals(id)) {
                if(book.getIssuedTo().equals(""))
                    rewriteFileInLibrary(file, builder.toString());
                break;
            }
        }
        if (book.getId() == null)
            return Optional.empty();
        return Optional.of(book);
    }

    @Override
    public Optional<Book> returnBook(Long id) {
        List<File> filesInLibraries = getFilesInLibraries();
        Book book = new Book();
        for (File file: filesInLibraries) {
            StringBuilder builder = new StringBuilder();
            book = createBookByIdIfExist(id,"","", builder, file);
            if (book.getId() != null && book.getId().equals(id)) {
                rewriteFileInLibrary(file, builder.toString());
                break;
            }
        }
        if (book.getId() == null)
            return Optional.empty();
        return Optional.of(book);
    }

    private List<File> getFilesInLibraries() {
        List<File> files = new ArrayList<>();
        for (File libraries : this.files) {
            if (!libraries.getName().startsWith("CSV_") || !libraries.isDirectory()
                    || Objects.requireNonNull(libraries.listFiles()).length <= 0)
                continue;
            for (File file : Objects.requireNonNull(libraries.listFiles())) {
                if (file.isDirectory())
                    continue;
                files.add(file);
            }
        }
         return files;
    }

    private Book createBookByIdIfExist(Long id, String issued, String issuedTo, StringBuilder builder, File file) {
        Book book = new Book();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String bookParamsValue = reader.readLine();
                String[] values = bookParamsValue.split(",");
                if (id.longValue() != Long.valueOf(values[0]).longValue()) {
                    builder.append(bookParamsValue).append("\n");
                    continue;
                }
                if (values.length == 3)
                    book = new Book(id,values[1],values[2],"","");
                else
                    book = new Book(id,values[1],values[2],values[3],values[4]);
                builder.append(book.getId()).append(",")
                        .append(book.getAuthor()).append(",")
                        .append(book.getName()).append(",")
                        .append(issued).append(",")
                        .append(issuedTo).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    private void rewriteFileInLibrary(File file, String infoInFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(infoInFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}