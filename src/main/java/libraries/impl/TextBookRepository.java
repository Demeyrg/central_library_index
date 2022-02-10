package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.TextBookMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TextBookRepository implements BookRepository {

    private TextBookMapper textBookMapper = new TextBookMapper();

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd");

    private static final File[] FILES = new File("src/main/resources/libraries/").listFiles();

    private static StringBuilder builder = new StringBuilder();

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        for (File file: FILES) {
            if (!file.getName().startsWith("Text_") || !file.isDirectory())
                continue;
            takeBookFromLibrary(file.listFiles(), books);
        }
        return books;
    }

    private void takeBookFromLibrary(File[] files, List<Book> books) {
        for(File file: files) {
            if (file.isDirectory())
                continue;
            Book book = textBookMapper.createBook(file);
            books.add(book);
        }
    }

    @Override
    public Book orderBook(Long id, String issuedTo) {
//        Book book = new Book();
//        for (File filesInLibrary: FILES) {
//            if (!filesInLibrary.getName().startsWith("Text_") || !filesInLibrary.isDirectory())
//                continue;
//            for(File file: filesInLibrary.listFiles()) {
//                if (file.isDirectory())
//                    continue;
//                book = orderBookFromLibrary(file,id,issuedTo);
//                if (book != null && book.getId().longValue() == id)
//                    return book;
//            }
//        }
        return null;
    }

//    private Book orderBookFromLibrary(File file, Long idSearch, String issuedTo) {
//        StringBuilder builder = new StringBuilder();
//        Book book = null;
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            while (reader.ready()) {
//                String paramAndValueRow = reader.readLine();
//                String[] paramAndValue = paramAndValueRow.split("=");
//                if (paramAndValue[0].equals("Index") && idSearch != Long.valueOf(paramAndValue[1]).longValue())
//                    break;
//                if (book == null)
//                    book = new Book();
//                if (emptyParam(paramAndValue) && paramAndValue[0].equals("Issued")) {
//                    textBookMapper.fillBook("Issued", FORMATTER.format(new Date()), book);
//                    builder.append(paramAndValueRow + book.getIssued() +"\n");
//                } else if (emptyParam(paramAndValue) && paramAndValue[0].equals("IssuedTo")) {
//                    textBookMapper.fillBook("IssuedTo", issuedTo, book);
//                    builder.append(paramAndValueRow + book.getIssuedTo());
//                } else {
//                    textBookMapper.fillBook(paramAndValue[0],paramAndValue[1],book);
//                    builder.append(paramAndValueRow + "\n");
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (book != null)
//            rewriteFileInLibrary(file, builder.toString());
//        return book;
//    }

    @Override
    public Optional<Book> returnBook(Long id) {
        List<File> filesInLibraries = getFilesInLibraries();
        Book book = new Book();
        for (File file: filesInLibraries) {
            book = createBookByIdIfExist(file,id,"","");
            if (book.getId() != null && book.getId().equals(id)) {
                rewriteFileInLibrary(file,builder.toString());
                break;
            }
        }
        if (book.getId() == null)
            return Optional.empty();
        return Optional.of(book);
    }

    private List<File> getFilesInLibraries() {
        List<File> files = new ArrayList<>();
        for (File libraries : FILES) {
            if (!libraries.getName().startsWith("Text_") || !libraries.isDirectory())
                continue;
            for (File file : libraries.listFiles()) {
                if (file.isDirectory())
                    continue;
                files.add(file);
            }
        }
        return files;
    }

    private Book createBookByIdIfExist(File file, Long id, String issued, String issuedTo) {
        Book book = new Book();
        builder.setLength(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String bookParamsValue = reader.readLine();
                String[] paramAndValue = bookParamsValue.split("=");
                if (paramAndValue[0].equals("Index") && id.longValue() != Long.valueOf(paramAndValue[1]).longValue()) {
                    break;
                }
                if (paramAndValue[0].equals("Issued")) {
                    book.setIssued(issued);
                    builder.append("Issued=" + issued +"\n");
                } else if (paramAndValue[0].equals("IssuedTo")) {
                    book.setIssued(issuedTo);
                    builder.append("IssuedTo=" + issuedTo);
                } else {
                    textBookMapper.fillBook(paramAndValue[0],paramAndValue[1],book);
                    builder.append(bookParamsValue + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
