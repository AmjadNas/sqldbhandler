package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class DBHandler {

    private DBHandler(){}

    public static <T> T build(Class<T> handlerClass) {
        try {

            if (!AnnotationProcessor.isHandler(handlerClass))
                throw new IllegalArgumentException(handlerClass.getSimpleName() + "is not a handler!");

            T handler = ConstructorUtils.invokeConstructor(handlerClass);
            ClassHelper classHelper = ClassHelper.getInstance();

            for (Class entity : AnnotationProcessor.getEntities(handlerClass)) {
                if (AnnotationProcessor.isEntity(entity))
                    classHelper.addClass(entity);
            }

            for (Field field : handlerClass.getDeclaredFields()) {
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
