package com.amjadnas.sqldbmanager.utills;

import com.amjadnas.sqldbmanager.annotations.Column;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassHelper {

    private static ClassHelper instance;
    private final Map<String, ClassInfo> classInfoHashMap;

    private ClassHelper() {
        classInfoHashMap = new HashMap<>();
    }

    public void addClass(Class clazz) {
        if (!AnnotationProcessor.isEntity(clazz))
            throw new IllegalArgumentException(clazz.getSimpleName() + "is not an entity!");

        ClassInfo classInfo = new ClassInfo(clazz.getName());

        Stream.of(clazz.getDeclaredFields())
                .filter(AnnotationProcessor::isColumn)
                .forEach(field -> {
                    classInfo.getters.put(field.getAnnotation(Column.class).name(), findMethod("get", field.getName(), clazz));
                    classInfo.setters.put(field.getAnnotation(Column.class).name(), findMethod("set", field.getName(), clazz));
                });


        classInfoHashMap.put(classInfo.name, classInfo);
    }

    public static ClassHelper getInstance() {
        if (instance == null) {
            synchronized (ClassHelper.class) {
                if (instance == null) {
                    instance = new ClassHelper();
                }
            }

        }
        return instance;
    }

    private class ClassInfo {
        private final String name;
        private final Map<String, Method> setters;
        private final Map<String, Method> getters;

        ClassInfo(String name) {
            this.name = name;
            getters = new HashMap<>();
            setters = new HashMap<>();
        }

        @Override
        public String toString() {
            return "ClassInfo{" +
                    "name='" + name + '\'' +
                    ", setters=" + setters +
                    ", getters=" + getters +
                    '}';
        }
    }

    private Method findMethod(String type, String fieldName, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if ((method.getName().startsWith(type)) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    return method;
                }
            }
        }
        return null;
    }

    public static <E> Object runSetter(String columnName, E o, Object value) {

        try {
            return instance.classInfoHashMap.get(o.getClass().getName()).setters.get(columnName).invoke(o, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        }

        return null;
    }



    public static <T> Object runGetter(String columnName, T o) {
        try {

            return instance.classInfoHashMap.get(o.getClass().getName()).getters.get(columnName).invoke(o);

        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        }

        return null;
    }


    public static <T>List<Pair> getFields(T obj){
        return Stream.of(obj.getClass().getDeclaredFields())
                .filter(field ->  field.isAnnotationPresent(Column.class))
                .map(field -> new Pair<String, Object>(field.getAnnotation(Column.class).name()
                        , ClassHelper.runGetter(field.getAnnotation(Column.class).name(), obj)))
                .collect(Collectors.toList());
    }
}
