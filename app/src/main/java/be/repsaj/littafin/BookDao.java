package be.repsaj.littafin;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM book")
    List<Book> getAll();

    @Query("DELETE FROM book")
     void reset();

    @Query("SELECT * FROM book WHERE title LIKE :title LIMIT 1")
    Book findByTitle(String title);

    @Query("DELETE  FROM book WHERE uid LIKE :uid ")
    void deleteByUid(int uid);

    @Query("SELECT * FROM book WHERE category LIKE :category ")
    List<Book> findByCategory(String category);

    @Query("SELECT DISTINCT category FROM book")
    Cursor getAllCategories();

    @Query("UPDATE book SET title = :title, author = :author, category = :category WHERE uid LIKE :uid")
    void update(int uid,String title,String author,String category);

    @Insert
    void insertAll(Book... books);
}