package com.github.amjadnas.sqldbmanager.builder.queryhandlers;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
@Deprecated(since = "0.1.0")
public interface QueryHandler<T> {

     T handleQuery(Connection connection, String query, Class<?> cls, Object...whereArgs) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException;
}
