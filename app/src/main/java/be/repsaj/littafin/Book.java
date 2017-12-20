package be.repsaj.littafin;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Book {

    @PrimaryKey(autoGenerate=true)
    private int uid;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "cover")
    private String cover;
    @ColumnInfo(name = "category")
    private String category;
    /*@ColumnInfo(name = "serie")
    private Boolean serie;
    @ColumnInfo(name = "serie_name")
    private String serieName;*/

    public Book(String title, String author, String category, String cover) {
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.category = category;
        //this.serie = serie;
        //this.serieName = serieName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

   public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /*public Boolean getSerie() {
        return serie;
    }

    public void setSerie(Boolean serie) {
        this.serie = serie;
    }

    public String getSerieName() {
        return serieName;
    }

    public void setSerieName(String serieName) {
        this.serieName = serieName;
    }*/
}
