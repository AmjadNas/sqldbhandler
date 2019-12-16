package com.github.amjadnas.sqldbmanager.builder;

import com.github.amjadnas.sqldbmanager.annotations.Entity;
import com.github.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.github.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class DeleteInterceptor implements QueryInterceptor {

    private Class returnType;
    private String[] keys;

    DeleteInterceptor(Class returnType, String[] keys) {
        this.returnType = returnType;
        this.keys = keys;
    }

    /**
     * this method handles delete queries based on provided columns by the developer
     * if there were no whereArgs provided then an SQLException will be thrown
     * the first argument must be the values in the object to be deleted.
     * the object must be an Entity annotated object
     *
     * @param connection connection from the database
     * @param whereArgs  the values of the arguments for the "where" clause
     * @return if the delete method was specified to return an int then the method will return how many rows have changed
     * @throws SQLException if there were database errors
     */
    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws SQLException {
        Object object = whereArgs[0];
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity, the first element in the array must be an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        String delete = QueryBuilder.deleteQuery(entityAnnot.name(), keys);
        Object[] values = new Object[whereArgs.length - 1];
        System.arraycopy(whereArgs, 1, values, 0, values.length);

        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
            for (Object p : values) {
                preparedStatement.setObject(i, p);
                i++;
            }
            i = preparedStatement.executeUpdate();
        }

        if (returnType.isPrimitive()) {
            return i;
        }

        return object;
    }

    /**
     * this method deletes the row of the provided object based on the primary key provided by the developer
     *
     * @param connection connection provided form the database
     * @param object     the object to be deleted in the database
     * @return if the return type was an int it the method will return the number of affected rows
     * else it will return the object that was provided by the user
     * @throws SQLException if there were errors in the delete process
     */
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
            for (Object p : values) {
                preparedStatement.setObject(i, p);
                i++;
            }
            i = preparedStatement.executeUpdate();
        }
        if (returnType.isPrimitive()) {
            return i;
        }


        return object;
    }
}
