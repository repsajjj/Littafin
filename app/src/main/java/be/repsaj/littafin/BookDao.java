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

    /*
    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
    */

    @Query("SELECT * FROM book WHERE title LIKE :title LIMIT 1")
    Book findByTitle(String title);

    @Query("SELECT * FROM book WHERE category LIKE :category ")
    Book findByCategory(String category);

    @Query("SELECT DISTINCT category FROM book")
    Cursor getAllCategories();

    @Insert
    void insertAll(Book... books);

    @Delete
    void delete(Book user);
}