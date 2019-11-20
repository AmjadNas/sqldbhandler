package com.amjadnas.sqldbmanager;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public interface Interceptor {
    @RuntimeType
     Object intercept(Connection connection, Object...whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException;
}
