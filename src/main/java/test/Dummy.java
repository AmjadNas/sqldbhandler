package test;


import com.amjadnas.sqldbmanager.annotations.Dao;
import com.amjadnas.sqldbmanager.annotations.Query;

import java.sql.Connection;
import java.util.List;

@Dao
public interface Dummy {

    @Query("HIIIII")
    List<User> getAll(Connection connection, Object...whArgs);
    @Query("BIIIIIIII")
    User getme(Connection connection, Object...whArgs);
}
