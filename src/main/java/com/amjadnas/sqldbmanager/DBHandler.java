package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.annotations.Dao;
import com.amjadnas.sqldbmanager.annotations.Handler;
import com.amjadnas.sqldbmanager.annotations.Query;
import com.amjadnas.sqldbmanager.utills.ClassHelper;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DBHandler {

    public static <T> T build(Class<T> handlerClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        T handler = ConstructorUtils.invokeConstructor(handlerClass);
        ClassHelper classHelper = ClassHelper.getInstance();

        for (Class entity : handlerClass.getAnnotation(Handler.class).entities()) {
            classHelper.addClass(entity);
        }


        for (Field field : handlerClass.getDeclaredFields()) {
            if (field.getType().isAnnotationPresent(Dao.class)){
                field.setAccessible(true);

                field.set(handler, DaoBuilder.buildDao(field.getType()));

                field.setAccessible(false);
            }
        }

        return handler;
    }

}
