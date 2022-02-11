package services;

public interface BookService {
    void findBookByOneParam(String param);

    void findBookByTwoParam(String paramOne, String paramTwo);

    void orderBook(String[] params);

    void returnBook(String id);
}
