package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.utills.ClassHelper;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

final class SingleQueryInterceptor implements QueryInterceptor {
    private Class<?> returnType;
    private String query;

    SingleQueryInterceptor(Class<?> returnType, String query) {
        this.returnType = returnType;
        this.query = query;
    }

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Object obj = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int bound = whereArgs.length;
            for (int i = 0; i < bound; i++) {
                preparedStatement.setObject(i+1, whereArgs[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                if (resultSet.next()) {
                    obj = ConstructorUtils.invokeConstructor(returnType);
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

    @Override
    public Object intercept2(Connection connection, Object object) {
        return null;
    }
}
