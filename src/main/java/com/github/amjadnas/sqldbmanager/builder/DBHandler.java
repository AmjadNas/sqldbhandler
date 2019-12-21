package com.github.amjadnas.sqldbmanager.builder;

import com.github.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * The class is used to build the database handler that enables to access the DAOs defend by the user
 */
public final class DBHandler {

    private DBHandler(){}

    /**
     * this method is used to build database handler all the user has to is provide a handler class to be build
     * be aware that every field in the handler class won't be instantiated if it's not annotated as a @Dao
     * the DAOs must be provided as interfaces otherwise an IllegalArgumentException will be thrown.
     * the classes that are provided in the @Handler annotation must be Entities
     * otherwise an IllegalArgumentException will be thrown.
     * @param handlerClass Class object of the handler to be built
     * @param <T> database handler class type
     * @return the instance of the handler class
     */
    public static <T> T build(Class<T> handlerClass) {
        try {

            if (!AnnotationProcessor.isHandler(handlerClass))
                throw new IllegalArgumentException(handlerClass.getSimpleName() + " is not a handler!");

            T handler = ConstructorUtils.invokeConstructor(handlerClass);
            ClassHelper2 classHelper = ClassHelper2.getInstance();
            // load in the objects that represent the entities in the database
            for (Class entity : AnnotationProcessor.getEntities(handlerClass)) {
                if (AnnotationProcessor.isEntity(entity))
                    ClassHelper2.addClass(entity);
            }

            for (Field field : handlerClass.getDeclaredFields()) {
                if (!field.getType().isInterface())
                    throw new IllegalArgumentException(field.getType().getSimpleName() + " must be an interface!");
                if (AnnotationProcessor.isDao(field)){
                    field.setAccessible(true);
                    field.set(handler, DaoBuilder.buildDao(field.getType()));
                    field.setAccessible(false);
                }
            }

            return handler;


        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e ) {
            e.printStackTrace();
            return null;
        }


    }

}
