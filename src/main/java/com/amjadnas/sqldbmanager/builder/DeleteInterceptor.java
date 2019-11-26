package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DeleteInterceptor implements QueryInterceptor{

    private Class returnType;
    private String[] keys;

    public DeleteInterceptor(Class returnType, String[] keys) {
        this.returnType = returnType;
        this.keys = keys;
    }

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Object object = whereArgs[0];
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity, the first element in the array must be an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        String delete = QueryBuilder.deleteQuery(entityAnnot.name(), keys);
        List<Object> values = Stream.of(keys)
                .map(key -> ClassHelper2.runGetter(key, object))
                .collect(Collectors.toList());

        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)){
            for (Object p : values) {
                preparedStatement.setObject(i, p);
                i++;
            }
           i =  preparedStatement.executeUpdate();
        }

        if (returnType.isPrimitive()){
            return i;
        }

        return object;
    }

    @Override
    public Object intercept2(Connection connection, Object object) throws SQLException {
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);
        String[] primaryKey = entityAnnot.primaryKey();
        String delete = QueryBuilder.deleteQuery(entityAnnot.name(), primaryKey);
        List<Object> values = Stream.of(primaryKey)
                                .map(key -> ClassHelper2.runGetter(key, object))
                                .collect(Collectors.toList());
        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)){
           for (Object p : values) {
              preparedStatement.setObject(i, p);
              i++;
           }
           preparedStatement.executeUpdate();
        }
        if (returnType.isPrimitive()){
            return i;
        }


        return object;
    }
}
