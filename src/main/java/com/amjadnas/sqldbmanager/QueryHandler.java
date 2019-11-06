package com.amjadnas.sqldbmanager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public interface QueryHandler<T> {

    T handleQuery(Connection connection, String query, Class<?> cls) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
