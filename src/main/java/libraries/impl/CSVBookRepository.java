package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.CSVBookMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CSVBookRepository implements BookRepository {

    private CSVBookMapper CSVbookMapper = new CSVBookMapper();

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd");

    private static final File[] FILES = new File("src/main/resources/libraries/").listFiles();

    private static StringBuilder builder = new StringBuilder();

    private static List<Book> allBooks;

    @Override
    public List<Book> getAllBooks() {
        allBooks = new ArrayList<>();
        for (File file: FILES) {
            if (!file.getName().startsWith("CSV_") || !file.isDirectory())
                continue;
            takeBookFromLibrary(file.listFiles());
        }
        return allBooks;
    }

    private void takeBookFromLibrary(File[] filesInLibrary) {
        List<Book> books = new ArrayList<>();
        for(File file: filesInLibrary) {
            if (file.isDirectory())
                continue;
            books.addAll(CSVbookMapper.createListBooks(file));
        }
        for (Book book:books)
            allBooks.add(book);
    }

//    @Override
//    public Book orderBook(Long id, String issuedTo) {
//        Book book = new Book();
//        for (File Libraries: FILES) {
//            if (!Libraries.getName().startsWith("CSV_") || !Libraries.isDirectory())
//                continue;
//            for(File file: Libraries.listFiles()) {
//                if (file.isDirectory())
//                    continue;
//                book = orderBookFromLibrary(file,id,issuedTo);
//                if (book != null && book.getId().longValue() == id)
//                    return book;
//            }
//        }
//        return book;
//    }

    @Override
    public Book orderBook(Long id, String issuedTo) {
        List<File> filesInLibraries = getFilesInLibraries();
        String issued = FORMATTER.format(new Date());
        Book book = new Book();
        for (File file: filesInLibraries) {
            book = createBookByIdIfExist(file,id,issued,issuedTo);
            if (book.getId() == id) {
                if(book.getIssuedTo().equals(""))
                    rewriteFileInLibrary(file,builder.toString());
                book.setIssuedTo(issuedTo);
                book.setIssued(issued);
                break;
            }
        }
        if (book.getId() == null)
            return null;
        return book;
    }

//    private Book orderBookFromLibrary(File file,long id, String issuedTo) {
//        StringBuilder builder = new StringBuilder();
//        Book book = null;
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            while (reader.ready()) {
//                String line = reader.readLine();
//                String[] splitLine = line.split(",");
//                long idBookInLibrary = Long.valueOf(splitLine[0]);
//                String author = splitLine[1];
//                String name = splitLine[2];
//                if (splitLine.length == 3 && id == idBookInLibrary) {
//                    String issued = FORMATTER.format(new Date());
//                    builder.append(id + ",").append(author + ",").append(name + ",").append(issued + ",").append(issuedTo + "\n");
//                    book = new Book(id, author, name, issued, issuedTo);
//                } else if (splitLine.length == 5 && id == idBookInLibrary) {
//                    String issued = splitLine[3];
//                    String issuedToAnother = splitLine[4];
//                    builder.append(line + "\n");
//                    book = new Book(id, author, name, issued, issuedToAnother);
//                } else {
//                    builder.append(line + "\n");
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
            if (!libraries.getName().startsWith("CSV_") || !libraries.isDirectory())
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
                String[] values = bookParamsValue.split(",");
                if (id.longValue() != Long.valueOf(values[0]).longValue()) {
                    builder.append(bookParamsValue + "\n");
                    continue;
                }
                if (values.length == 3)
                    book = new Book(id,values[1],values[2],"","");
                else
                    book = new Book(id,values[1],values[2],values[3],values[4]);
                builder.append(book.getId() + ",")
                        .append(book.getAuthor() + ",")
                        .append(book.getName() + ",")
                        .append(issued + ",")
                        .append(issuedTo + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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