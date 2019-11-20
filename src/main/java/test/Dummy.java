package test;


import com.amjadnas.sqldbmanager.annotations.Dao;
import com.amjadnas.sqldbmanager.annotations.Insert;
import com.amjadnas.sqldbmanager.annotations.Query;

import java.sql.Connection;
import java.util.List;

@Dao
public interface Dummy {

    @Query("SELECT id from movie")
    List<Movie> getAll(Connection connection, Object... whArgs);

    @Query("SELECT id from movie where id = ?")
    Movie getme(Connection connection, Object... whArgs);

    @Insert
    Movie insert(Connection connection, Movie move);
}
