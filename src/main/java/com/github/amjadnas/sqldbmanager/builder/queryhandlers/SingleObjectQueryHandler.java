package com.github.amjadnas.sqldbmanager.builder.queryhandlers;

import com.github.amjadnas.sqldbmanager.utills.ClassHelper;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
@Deprecated(since = "0.1.0")
 class SingleObjectQueryHandler<E> implements QueryHandler<E> {

    @Override
    public E handleQuery(Connection connection, String query, Class<?> cls, Object...whereArgs) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        E obj = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int bound = whereArgs.length;
            for (int i = 0; i < bound; i++) {
                preparedStatement.setObject(i+1, whereArgs[i]);
            }

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

            }
        }


        return obj;
    }
}
