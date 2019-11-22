package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class UpdateInterceptor implements QueryInterceptor{

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return null;
    }

    @Override
    public Object intercept2(Connection connection, Object object) throws SQLException {
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair<String, Object>> pairs = ClassHelper.getFields(object);
        String update = QueryBuilder.updateQuery(entityAnnot.name(), "", pairs);

        try (PreparedStatement preparedStatement = connection.prepareStatement(update)){
            //TODO add support for enum and blobs
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }

            preparedStatement.executeUpdate();
        }

        return object;
    }
}
