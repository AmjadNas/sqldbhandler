package com.amjadnas.sqldbmanager.queryhandlers;

import com.amjadnas.sqldbmanager.annotations.Query;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public interface QueryHandler<T> {

     T handleQuery(Connection connection, String query, Class<?> cls, Object...whereArgs) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException;
}
