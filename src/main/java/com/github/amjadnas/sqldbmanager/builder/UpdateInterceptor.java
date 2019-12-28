package com.github.amjadnas.sqldbmanager.builder;

import com.github.amjadnas.sqldbmanager.annotations.Entity;
import com.github.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.github.amjadnas.sqldbmanager.utills.Pair;
import com.github.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class UpdateInterceptor implements QueryInterceptor {

    private Class returnType;
    private String[] keys;

    UpdateInterceptor(Class returnType, String[] keys) {
        this.returnType = returnType;
        this.keys = keys;
    }

    /**
     * this method handles update queries based on provided columns by the developer
     * if there were no whereArgs provided then an SQLException will be thrown
     * the first argument must be the values in the object to be updated.
     * the object must be an Entity annotated object
     *
     * @param connection connection from the database
     * @param whereArgs  the values of the arguments for the "where" clause
     * @return if the update method was specified to return an int then the method will return how many rows have changed
     * @throws SQLException if there were database errors
     */
    @Override
    public Object handleQueryWithArgs(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Object object = whereArgs[0];
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity, the first element in the array must be an Entity");

        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair<String, Object>> pairs = ClassHelper2.getFields(object);
        Object[] objects = new Object[whereArgs.length - 1];

        System.arraycopy(whereArgs, 1, objects, 0, objects.length);
        String update = QueryBuilder.updateQuery(entityAnnot.name(), keys, pairs);

        try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }
            for (Object p : objects) {
                preparedStatement.setObject(i, p);
                i++;
            }
            i = preparedStatement.executeUpdate();
        }

        if (returnType.isPrimitive())
            return i;

        return object;
    }

    /**
     * this method updates the row of the provided object based on the primary key provided by the developer
     *
     * @param connection connection provided form the database
     * @param object     the object to be updated in the database
     * @return if the return type was an int it the method will return the number of affected rows
     * else it will return the object that was provided by the user
     * @throws SQLException if there were errors in the update process
     */
    @Override
    public Object handleQuery(Connection connection, Object object) throws SQLException {
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
                if (pair.first.equals(key)) {
                    values.add(pair.second);
                }
        }

        String update = QueryBuilder.updateQuery(entityAnnot.name(), primaryKey, pairs);

        try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
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
