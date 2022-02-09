package libraries.impl;

import entity.Book;
import libraries.BookRepository;
import mapper.CSVBookMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVBookRepository implements BookRepository {

    private CSVBookMapper CSVbookMapper = new CSVBookMapper();

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

    private static final File[] files = new File("src/main/resources/libraries/").listFiles();

    private static List<Book> allBooks;

    @Override
    public List<Book> getAllBooks() {
        allBooks = new ArrayList<>();
        for (File file: files) {
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

    @Override
    public Book orderBook(Long id, String issuedTo) {
        Book book = new Book();
        for (File filesInLibrary: files) {
            if (!filesInLibrary.getName().startsWith("CSV_") || !filesInLibrary.isDirectory())
                continue;
            for(File file: filesInLibrary.listFiles()) {
                if (file.isDirectory())
                    continue;
                book = orderBookFromLibrary(file,id,issuedTo);
                if (book != null && book.getId().longValue() == id)
                    return book;
            }
        }
        return book;
    }

    private Book orderBookFromLibrary(File file,long id, String issuedTo) {
        StringBuilder builder = new StringBuilder();
        Book book = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] splitLine = line.split(",");
                long idBookInLibrary = Long.valueOf(splitLine[0]);
                String author = splitLine[1];
                String name = splitLine[2];
                if (splitLine.length == 3 && id == idBookInLibrary) {
                    String issued = formatter.format(new Date());
                    builder.append(id + ",").append(author + ",").append(name + ",").append(issued + ",").append(issuedTo + "\n");
                    book = new Book(id, author, name, issued, issuedTo);
                } else if (splitLine.length == 5 && id == idBookInLibrary) {
                    String issued = splitLine[3];
                    String issuedToAnother = splitLine[4];
                    builder.append(line + "\n");
                    book = new Book(id, author, name, issued, issuedToAnother);
                } else {
                    builder.append(line + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (book != null)
            changeFileInLibrary(file, builder.toString());
        return book;
    }

    private void changeFileInLibrary(File file, String toString) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnBook(Book book) {

    }
}


//УДалить старый метод
//        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
//            StringBuilder builder = new StringBuilder();
//            String line;
//            long pointer = 0;
//            while ((line = randomAccessFile.readLine()) != null) {
//                String[] splitLine = line.split(",");
//                long idBookInLibrary = Long.valueOf(splitLine[0]);
//                String author = splitLine[1];
//                String name = splitLine[2];
//                if (splitLine.length == 3 && id == idBookInLibrary) {
//                    String issued = formatter.format(new Date());
//                    builder.append(id + ",").append(author + ",").append(name + ",").append(issued +  ",").append(issuedTo);
//                    randomAccessFile.seek(pointer);
//                    randomAccessFile.write(builder.toString().getBytes(StandardCharsets.UTF_8));
//                    return new Book(id,author,name,issued,issuedTo);
//                } else if (splitLine.length == 5 && id == idBookInLibrary) {
//                    String issued = splitLine[3];
//                    String issuedToAnother = splitLine[4];
//                    return new Book(id,author,name,issued,issuedToAnother);
//                }
//                pointer = randomAccessFile.getFilePointer();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new Book();
