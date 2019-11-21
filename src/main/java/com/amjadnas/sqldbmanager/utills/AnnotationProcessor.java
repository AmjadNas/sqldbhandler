package com.amjadnas.sqldbmanager.utills;

import com.amjadnas.sqldbmanager.annotations.*;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import com.amjadnas.sqldbmanager.utills.Pair;
import com.amjadnas.sqldbmanager.utills.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AnnotationProcessor {

    private AnnotationProcessor(){}

    public static boolean isEntity(Class<?> clazz){
        return clazz.isAnnotationPresent(Entity.class);
    }

    public static boolean isDao(Class<?> clazz){
        return clazz.isAnnotationPresent(Dao.class);
    }

    public static boolean isDao(Field clazz){
        return clazz.getType().isAnnotationPresent(Dao.class);
    }

    public static boolean isColumn(Field clazz){
        return clazz.isAnnotationPresent(Column.class);
    }

    public static boolean isQuery(Class<?> clazz){
        return clazz.isAnnotationPresent(Query.class);
    }

    public static boolean isQuery(Method clazz){
        return clazz.isAnnotationPresent(Query.class);
    }

    public static boolean isHandler(Class<?> clazz){
        return clazz.isAnnotationPresent(Handler.class);
    }

    public static boolean isInsert(Method method) {
        return method.isAnnotationPresent(Insert.class);
    }

    public static Class[] getEntities(Class<?> clazz){
        if (isHandler(clazz))
            return clazz.getAnnotation(Handler.class).entities();
        else
            throw new IllegalArgumentException(clazz.getSimpleName() + "is not a database handler");
    }

    static <T> void proccess(Connection connection, T user) throws SQLException {

        Class<?> obj = user.getClass();
        int i = 1;
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair> pairs = ClassHelper.getFields(user);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {

            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }

        }


    }
}