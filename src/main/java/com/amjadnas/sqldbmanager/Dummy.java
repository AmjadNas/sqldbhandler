package com.amjadnas.sqldbmanager;


import com.amjadnas.sqldbmanager.User;
import com.amjadnas.sqldbmanager.annotations.Dao;
import com.amjadnas.sqldbmanager.annotations.Query;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Dao
public interface Dummy {

    @Query("HIIIII")
    List<User> getAll(Connection connection, Object...whArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException;

}
