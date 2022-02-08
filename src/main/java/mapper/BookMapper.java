package mapper;

import entity.Book;

import java.io.File;

public interface BookMapper {
    Book createBook(File line);
}
