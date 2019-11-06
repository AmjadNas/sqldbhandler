package com.amjadnas.sqldbmanager;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class ListQueryHandler<E> implements QueryHandler<List<E>> {

    public List<E> handleQuery(Connection connection, String query, Class<?> cls) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        List<E> list = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    E obj = (E) ConstructorUtils.invokeConstructor(cls);
                    for (int i = 1; i <= colCount; i++) {
                        String className = metaData.getColumnClassName(i);
                        String columnName = metaData.getColumnName(i);

                        ClassHelper.runSetter(columnName, obj, resultSet.getObject(i, Class.forName(className)));

                    }
                    list.add(obj);
                }

            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }


        return list;
    }


}
