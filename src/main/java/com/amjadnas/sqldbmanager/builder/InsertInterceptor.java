package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

public final class InsertInterceptor implements QueryInterceptor {

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return null;
    }

    @Override
    public Object intercept2(Connection connection, Object object)  {
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair<String,Object>> pairs = ClassHelper.getFields(object);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            //TODO add support for enum and blobs
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }

            preparedStatement.executeUpdate();
            //if (entityAnnot) auto increment
           /* try (ResultSet generatedID = preparedStatement.getGeneratedKeys()) {
                if (generatedID.next())
                    ClassHelper.runSetter("key", object, generatedID.getInt(1))
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return object;
    }
}
