package com.amjadnas.sqldbmanager.builder;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.sql.Connection;
import java.sql.SQLException;

public interface ModificationInterceptor {

    @RuntimeType
    public Object intercept(Connection connection, Object obj) throws SQLException;
}
