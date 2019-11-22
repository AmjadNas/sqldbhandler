package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DeleteInterceptor implements QueryInterceptor{


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

        String delete = QueryBuilder.deleteQuery(entityAnnot.name(), "");

        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)){
            //TODO configure deletion
          //  for (Pair p : pairs) {
         //       preparedStatement.setObject(i, p.second);
         //       i++;
          //  }

            preparedStatement.executeUpdate();
        }

        return object;
    }
}
