package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertInterceptor implements QueryInterceptor {

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return null;
    }

    @Override
    public Object intercept2(Connection connection, Object object)  {
        Class<?> obj = object.getClass();
        int i = 1;
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair> pairs = ClassHelper.getFields(object);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {

            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return object;
    }
}
