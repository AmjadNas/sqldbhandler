package com.amjadnas.sqldbmanager.builder;

import java.sql.Connection;

public class DeleteInterceptor implements ModificationInterceptor{

    @Override
    public Object intercept(Connection connection, Object obj) {
        return null;
    }
}
