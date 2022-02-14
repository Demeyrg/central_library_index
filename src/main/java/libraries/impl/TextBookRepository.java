package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.TextBookMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TextBookRepository implements BookRepository {

    private final TextBookMapper textBookMapper = new TextBookMapper();

    private final File[] files;

    public TextBookRepository(File[] files) {
        this.files = files;
    }

    @Override
    public List<Book> getBooksByName(String name) {
        List<Book> allBooks = getAllBooksFromLibrariesText();
        List<Book> booksByParam = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getName().contains(name))
                booksByParam.add(book);
        }
        return booksByParam;
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        List<Book> allBooks = getAllBooksFromLibrariesText();
        List<Book> booksByParam = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getAuthor().contains(author))
                booksByParam.add(book);
        }
        return booksByParam;
    }

    @Override
    public List<Book> getBooksByAuthorAndName(String author, String name) {
        List<Book> allBooks = getAllBooksFromLibrariesText();
        List<Book> booksByParam = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getAuthor().contains(author) && book.getName().contains(name))
                booksByParam.add(book);
        }
        return booksByParam;
    }

    private List<Book> getAllBooksFromLibrariesText() {
        List<Book> books = new ArrayList<>();
        assert files != null;
        for (File libraries : files) {
            if (!libraries.getName().startsWith("Text_") || !libraries.isDirectory()
                    || Objects.requireNonNull(libraries.listFiles()).length <= 0)
                continue;
            for (File file : Objects.requireNonNull(libraries.listFiles())) {
                if (file.isDirectory())
                    continue;
                books.add(textBookMapper.returnBook(file));
            }
        }
        return books;
    }

    @Override
    public Optional<Book> orderBookInLibrary(Long id, String issued, String issuedTo) {
        List<File> filesInLibraries = getFilesInLibraries();
        StringBuilder builder = new StringBuilder();
        Book book = new Book();
        for (File file : filesInLibraries) {
            book = createBookByIdIfExist(id, issued, issuedTo, builder, file);
            if (book.getId() != null && book.getId().equals(id)) {
                if (book.getIssuedTo().equals(""))
                    rewriteFileInLibrary(file, builder.toString());
                break;
            }
        }
        if (book.getId() == null)
            return Optional.empty();
        return Optional.of(book);
    }

    @Override
    public Optional<Book> returnBookInLibrary(Long id) {
        List<File> filesInLibraries = getFilesInLibraries();
        StringBuilder builder = new StringBuilder();
        Book book = new Book();
        for (File file : filesInLibraries) {
            book = createBookByIdIfExist(id, "", "", builder, file);
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
        List<File> filesInLibrary = new ArrayList<>();
        for (File libraries : files) {
            if (!libraries.getName().startsWith("Text_") || !libraries.isDirectory()
                    || Objects.requireNonNull(libraries.listFiles()).length <= 0)
                continue;
            for (File file : Objects.requireNonNull(libraries.listFiles())) {
                if (file.isDirectory())
                    continue;
                filesInLibrary.add(file);
            }
        }
        return filesInLibrary;
    }

    private Book createBookByIdIfExist(Long id, String issued, String issuedTo, StringBuilder builder, File file) {
        Book book = new Book();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String bookParamsValue = reader.readLine();
                String[] paramAndValue = bookParamsValue.split("=");
                if (paramAndValue[0].equals("Index") && id.longValue() != Long.valueOf(paramAndValue[1]).longValue()) {
                    break;
                }
                if (paramAndValue[0].equals("Issued")) {
                    book.setIssued(paramAndValue.length == 2 ? paramAndValue[1] : "");
                    builder.append("Issued=").append(issued).append("\n");
                } else if (paramAndValue[0].equals("IssuedTo")) {
                    book.setIssuedTo(paramAndValue.length == 2 ? paramAndValue[1] : "");
                    builder.append("IssuedTo=").append(issuedTo);
                } else {
                    textBookMapper.fillBook(paramAndValue[0], paramAndValue[1], book);
                    builder.append(bookParamsValue).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    private void rewriteFileInLibrary(File file, String toString) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}