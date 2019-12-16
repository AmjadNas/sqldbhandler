package com.github.amjadnas.sqldbmanager.utills;

import com.github.amjadnas.sqldbmanager.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public static boolean isUpdate(Method method) {
        return method.isAnnotationPresent(Update.class);
    }

    public static boolean isDelete(Method method) {
        return method.isAnnotationPresent(Delete.class);
    }

    public static Class[] getEntities(Class<?> clazz){
        if (isHandler(clazz))
            return clazz.getAnnotation(Handler.class).entities();
        else
            throw new IllegalArgumentException(clazz.getSimpleName() + "is not a database handler");
    }


}
