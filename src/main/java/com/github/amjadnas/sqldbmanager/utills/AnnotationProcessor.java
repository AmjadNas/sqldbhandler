package com.github.amjadnas.sqldbmanager.utills;

import com.github.amjadnas.sqldbmanager.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class used to process the annotations
 */
public class AnnotationProcessor {

    private AnnotationProcessor(){}

    /**
     *
     * @param clazz the class object to be checked
     * @return if the provided class is an annotated entity object
     */
    public static boolean isEntity(Class<?> clazz){
        return clazz.isAnnotationPresent(Entity.class);
    }
    /**
     *
     * @param clazz the class object to be checked
     * @return if the provided class is an annotated Dao interface
     */
    public static boolean isDao(Class<?> clazz){
        return clazz.isAnnotationPresent(Dao.class);
    }
    /**
     *
     * @param clazz the class field to be checked
     * @return if the provided class is an annotated Dao interface
     */
    public static boolean isDao(Field clazz){
        return clazz.getType().isAnnotationPresent(Dao.class);
    }
    /**
     *
     * @param clazz the class field to be checked
     * @return if the provided class is an annotated Column field
     */
    public static boolean isColumn(Field clazz){
        return clazz.isAnnotationPresent(Column.class);
    }

    public static boolean isQuery(Class<?> clazz){
        return clazz.isAnnotationPresent(Query.class);
    }
    /**
     *
     * @param clazz the class method to be checked
     * @return if the provided class is an annotated Query method
     */
    public static boolean isQuery(Method clazz){
        return clazz.isAnnotationPresent(Query.class);
    }

    public static boolean isHandler(Class<?> clazz){
        return clazz.isAnnotationPresent(Handler.class);
    }
    /**
     *
     * @param method the class method to be checked
     * @return if the provided class is an annotated Insert method
     */
    public static boolean isInsert(Method method) {
        return method.isAnnotationPresent(Insert.class);
    }
    /**
     *
     * @param method the class method to be checked
     * @return if the provided class is an annotated Update method
     */
    public static boolean isUpdate(Method method) {
        return method.isAnnotationPresent(Update.class);
    }
    /**
     *
     * @param method the class method to be checked
     * @return if the provided class is an annotated Delete method
     */
    public static boolean isDelete(Method method) {
        return method.isAnnotationPresent(Delete.class);
    }
    /**
     *
     * @param clazz the class method to be checked
     * @return returns the entities registered to the handler class
     */
    public static Class[] getEntities(Class<?> clazz){
        if (isHandler(clazz))
            return clazz.getAnnotation(Handler.class).entities();
        else
            throw new IllegalArgumentException(clazz.getSimpleName() + "is not a database handler");
    }


}
