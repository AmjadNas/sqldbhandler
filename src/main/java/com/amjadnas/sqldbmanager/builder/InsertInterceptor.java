package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

public final class InsertInterceptor implements QueryInterceptor {

    private Class returnType;

    public InsertInterceptor(Class returnType) {
        this.returnType = returnType;
    }

    @Override
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        throw new RuntimeException("Wrong insert method signature. Insert method arguments must be (Connection, Object) not (Connection, Object[])");
    }

    @Override
    public Object intercept2(Connection connection, Object object) throws SQLException {
        Class<?> obj = object.getClass();
        int i = 1;
        if (!AnnotationProcessor.isEntity(obj))
            throw new IllegalArgumentException(obj + "is not an Entity");
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair<String, Object>> pairs = ClassHelper2.getFields(object);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            //TODO add support and blobs
            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }
            i = preparedStatement.executeUpdate();
            if (returnType.isPrimitive())
                return i;

            if (i > 0 && entityAnnot.isAutoIncrement()) {
                try (ResultSet generatedID = preparedStatement.getGeneratedKeys()) {
                    if (generatedID.next())
                        ClassHelper2.runSetter(entityAnnot.primaryKey()[0], object, generatedID.getInt(1));
                }
            }

        }

        return object;
    }
}
