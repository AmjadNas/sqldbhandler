package com.amjadnas.sqldbmanager;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class SingleObjectQueryHandler<E> implements QueryHandler<E>{

    @Override
    public E handleQuery(Connection connection, String query, Class<?> cls) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        E obj = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                if (resultSet.next()) {
                    obj = (E) ConstructorUtils.invokeConstructor(cls);
                    for (int i = 1; i <= colCount; i++) {
                        String className = metaData.getColumnClassName(i);
                        String columnName = metaData.getColumnName(i);

                        ClassHelper.runSetter(columnName, obj, resultSet.getObject(i, Class.forName(className)));

                    }
                }

            } catch (ClassNotFoundException|ClassCastException e) {
                e.printStackTrace();
            }
        }


        return obj;
    }
}
