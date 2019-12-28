package com.github.amjadnas.sqldbmanager.builder;

import com.github.amjadnas.sqldbmanager.annotations.Entity;
import com.github.amjadnas.sqldbmanager.exceptions.IllegalMethodException;
import com.github.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.github.amjadnas.sqldbmanager.utills.Pair;
import com.github.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

final class InsertInterceptor implements QueryInterceptor {

    private Class returnType;

    InsertInterceptor(Class returnType) {
        this.returnType = returnType;
    }

    @Override
    public Object handleQueryWithArgs(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        throw new IllegalMethodException("Wrong insert method signature. Insert method arguments must be (Connection, Object) not (Connection, Object[])");
    }

    /**
     *
     * @param connection from the database to process the request
     * @param object the object to be inserted to the database. Note that if the object is an Entity then the method will throw an IllegalArgumentException
     * @return if the return type is int then the method will return int if the object has an auto-incremented id then it will return the generated id
     * else it will return the number of successful operations. if the reutrn type is an object then the method will return the object and
     * in case the object has auto-incremented id then it will return it with the generated id.
     * Note that the return type can be void.
     * @throws SQLException if the database throws a special sql error
     */
    @Override
    public Object handleQuery(Connection connection, Object object) throws SQLException {
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair<String, Object>> pairs = ClassHelper2.getFields(object);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }
            i = preparedStatement.executeUpdate();


            if (i > 0 && entityAnnot.isAutoIncrement()) {
                try (ResultSet generatedID = preparedStatement.getGeneratedKeys()) {
                    if (generatedID.next()) {
                        i = generatedID.getInt(1);
                        ClassHelper2.runSetter(entityAnnot.primaryKey()[0], object, i);
                    }
                }
            }

        }

        if (returnType.isPrimitive())
            return i;

        return object;
    }
}
