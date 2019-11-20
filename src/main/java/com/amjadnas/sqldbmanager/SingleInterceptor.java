package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.utills.ClassHelper;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class SingleInterceptor implements Interceptor{
    private Class<?> returnTupe;
    private String query;

    public SingleInterceptor(Class<?> returnTupe, String query) {
        this.returnTupe = returnTupe;
        this.query = query;
    }

    @RuntimeType
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
                    obj = ConstructorUtils.invokeConstructor(returnTupe);
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
