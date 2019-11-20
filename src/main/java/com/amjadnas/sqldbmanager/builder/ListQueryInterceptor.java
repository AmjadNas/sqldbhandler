package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.utills.ClassHelper;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

final class ListQueryInterceptor implements QueryInterceptor {

    private Class<?> returnTupe;
    private String query;

    ListQueryInterceptor(Type returnTupe, String query) {
        ParameterizedType pt = (ParameterizedType) returnTupe;
        this.returnTupe = (Class)pt.getActualTypeArguments()[0];
        this.query = query;
    }

    @RuntimeType
    @Override
    public List<Object> intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        List<Object> list = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int bound = whereArgs.length;
            for (int i = 0; i < bound; i++) {
                preparedStatement.setObject(i + 1, whereArgs[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Object obj = ConstructorUtils.invokeConstructor(returnTupe);
                    for (int i = 1; i <= colCount; i++) {
                        String className = metaData.getColumnClassName(i);
                        String columnName = metaData.getColumnName(i);

                        ClassHelper.runSetter(columnName, obj, resultSet.getObject(i, Class.forName(className)));

                    }
                    list.add(obj);
                }

            }
            return list;
        }
    }
}
