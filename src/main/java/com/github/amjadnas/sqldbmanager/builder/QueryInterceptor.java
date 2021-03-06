package com.github.amjadnas.sqldbmanager.builder;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public interface QueryInterceptor {
    @RuntimeType
    Object handleQueryWithArgs(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException;

    @RuntimeType
    Object handleQuery(Connection connection, Object object) throws SQLException;
}
