package com.amjadnas.sqldbmanager.builder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteInterceptor implements QueryInterceptor{


    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return null;
    }

    @Override
    public Object intercept2(Connection connection, Object object) {
        return null;
    }
}
