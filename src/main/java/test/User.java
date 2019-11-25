package test;

import com.amjadnas.sqldbmanager.annotations.Column;
import com.amjadnas.sqldbmanager.annotations.Entity;

@Entity(name = "users", primaryKey = {"username"})
public class User {


    public User(String name) {
        this.name = name;
    }

    @Column(name = "username")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
