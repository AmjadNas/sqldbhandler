package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UpdateInterceptor implements QueryInterceptor{

    private Class returnType;
    private String[] keys;

    public UpdateInterceptor(Class returnType, String[] keys) {
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

        List<Pair<String, Object>> pairs = ClassHelper2.getFields(object);
        Object[] objects = new Object[whereArgs.length-1];

        System.arraycopy(whereArgs, 1, objects, 0, objects.length);
        String update = QueryBuilder.updateQuery(entityAnnot.name(), keys, pairs);

        try (PreparedStatement preparedStatement = connection.prepareStatement(update)){
            //TODO add support for enum and blobs
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }
            for (Object p : objects) {
                preparedStatement.setObject(i, p);
                i++;
            }
            i =  preparedStatement.executeUpdate();
        }

        if (returnType.isPrimitive())
            return i;

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
        List<Pair<String, Object>> pairs = ClassHelper2.getFields(object);
        List<Object> values = new ArrayList<>();

        for (String key : primaryKey) {
            for (Pair pair : pairs)
                if (pair.first.equals(key)){
                    values.add(pair.second);
                }
        }

        String update = QueryBuilder.updateQuery(entityAnnot.name(), primaryKey, pairs);

        try (PreparedStatement preparedStatement = connection.prepareStatement(update)){
            //TODO add support for enum and blobs
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }
            for (Object p : values) {
                preparedStatement.setObject(i, p);
                i++;
            }
            i = preparedStatement.executeUpdate();
        }
        if (returnType.isPrimitive())
            return i;
        return object;
    }
}
