package com.amjadnas.sqldbmanager.utills;

import com.amjadnas.sqldbmanager.annotations.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");
        if (!AnnotationProcessor.isEntity(clazz))
            throw new IllegalArgumentException(clazz.getSimpleName() + "is not an entity!");

        ClassInfo classInfo = new ClassInfo(clazz.getName());

        Stream.of(clazz.getDeclaredFields())
                .filter(AnnotationProcessor::isColumn)
                .forEach(field -> {
                    String fieldColumnName = field.getAnnotation(Column.class).name();
                    classInfo.fields.put(fieldColumnName, field);
                    classInfo.getters.put(fieldColumnName, findMethod("get", field.getName(), clazz));
                    classInfo.setters.put(fieldColumnName, findMethod("set", field.getName(), clazz));
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
        private final Map<String, Field>  fields;

        ClassInfo(String name) {
            this.name = name;
            getters = new IdentityHashMap<>();
            setters = new IdentityHashMap<>();
            fields = new IdentityHashMap<>();
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
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");

        if (value == null)
            return null;

        try {
            String className = o.getClass().getName();
            ClassInfo classInfo = instance.classInfoHashMap.get(className);
            Class clazz = classInfo.fields.get(className).getType();
            if (classInfo.fields.get(className).isEnumConstant()){

                value = Enum.valueOf((Class<Enum>)clazz, value.toString());
            }

            return classInfo.setters.get(columnName).invoke(o, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        }

        return null;
    }



    public static <T> Object runGetter(String columnName, T o) {
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");
        try {

            String className = o.getClass().getName();
            ClassInfo classInfo = instance.classInfoHashMap.get(className);
            Object object = classInfo.getters.get(columnName).invoke(o);
            if (classInfo.fields.get(className).isEnumConstant()) {
                return object.toString();
            }
                return object;

        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        }

        return null;
    }


    public static <T>List<Pair<String, Object>> getFields(T obj){

        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");

        return instance.classInfoHashMap.get(obj.getClass().getName())
                .fields.entrySet().stream()
                .map((entry) -> convertToPairs(entry, obj))
                .filter(pair -> pair.second != null)
                .collect(Collectors.toList());
    }

    private static Pair<String, Object> convertToPairs(Map.Entry<String, Field> entry, Object obj){
        Object value = runGetter(entry.getKey(), obj);

        if (entry.getValue().isEnumConstant() && value != null)
            value = value.toString();

        return new Pair<>(entry.getKey(), value);
    }
}
