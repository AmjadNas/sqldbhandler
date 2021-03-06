package test;

import com.github.amjadnas.sqldbmanager.annotations.Column;
import com.github.amjadnas.sqldbmanager.annotations.Entity;

import java.sql.Time;

@Entity(name="movie", primaryKey = "id", isAutoIncrement = true)
public class Movie {

    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="category")
    private Category category;

    @Column(name="length")
    private Time length;

    @Column(name="producer")
    private Integer producer;

    @Column(name="image")
    byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Movie(){

    }

    public Movie(Integer id, String name, Category category, Time length, Integer producer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.length = length;
        this.producer = producer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Time getLength() {
        return length;
    }

    public void setLength(Time length) {
        this.length = length;
    }

    public Integer getProducer() {
        return producer;
    }

    public void setProducer(Integer producer) {
        this.producer = producer;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", length=" + length +
                ", producer=" + producer +
                " vyt " + image +
                '}';
    }
}