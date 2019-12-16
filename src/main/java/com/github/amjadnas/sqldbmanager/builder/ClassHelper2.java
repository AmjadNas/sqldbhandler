package com.github.amjadnas.sqldbmanager.builder;

import com.github.amjadnas.sqldbmanager.annotations.Column;
import com.github.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.github.amjadnas.sqldbmanager.utills.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class used to execute getter and setter
 */
class ClassHelper2 {

    private static ClassHelper2 instance;
    private static final Map<String, ClassInfo> classInfoHashMap = new HashMap<>();
    private static final MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

    private ClassHelper2() {

    }

    /**
     * stores class information for further use
     * @param clazz a class that is annotated as Entity
     */
    static void addClass(Class<?> clazz) {
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
                    classInfo.getters.put(fieldColumnName, findMethod("get", field, clazz));
                    classInfo.setters.put(fieldColumnName, findMethod("set", field, clazz));
                });


        classInfoHashMap.put(classInfo.name, classInfo);
    }

    static ClassHelper2 getInstance() {
        if (instance == null) {
            synchronized (ClassHelper2.class) {
                if (instance == null) {
                    instance = new ClassHelper2();
                }
            }

        }
        return instance;
    }

    private static class ClassInfo {
        private final String name;
        private final Map<String, MethodHandle> setters;
        private final Map<String, MethodHandle> getters;
        private final Map<String, Field> fields;

        ClassInfo(String name) {
            this.name = name;
            getters = new HashMap<>();
            setters = new HashMap<>();
            fields = new HashMap<>();
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

    /**
     *
     * @param type type of method setter or a getter
     * @param field field name
     * @param clazz the class object to be looked through
     * @return method handle for the getter or the setter
     */
    private static MethodHandle findMethod(String type, Field field, Class<?> clazz) {

        try {
           // String methodName = "";
            for (Method method : clazz.getDeclaredMethods()) {
                if ((method.getName().startsWith(type)) && (method.getName().length() == (field.getName().length() + 3))) {
                    if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {

                        return  publicLookup.unreflect(method);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * executes the setter based on the provided column name and object
     * @param columnName the name of the column that corresponds with the class field
     * @param o the object the setter to be executed on
     * @param value the value to be set
     */
    static void runSetter(String columnName, Object o, Object value) {
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");

        if (value == null)
            return;

        try {
            String className = o.getClass().getName();
            ClassInfo classInfo = classInfoHashMap.get(className);
            Field field = classInfo.fields.get(columnName);
            Class clazz = field.getType();
            if (clazz.isEnum()) {

                 value = Enum.valueOf(clazz, value.toString());
               //value = null;
            }


             classInfo.setters.get(columnName).invoke(o,value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    /**
     * executes the getter based on the provided column name and object
     * @param columnName the name of the column that corresponds with the class field
     * @param o the object the getter to be executed on
     */
    static Object runGetter(String columnName, Object o) {
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");
        try {

            String className = o.getClass().getName();
            ClassInfo classInfo = classInfoHashMap.get(className);
            Object object = classInfo.getters.get(columnName).invoke(o);
            if (object != null && object.getClass().isEnum()) {
                return object.toString();
            }
            return object;

        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }

    /**
     * returns key value pairs of the column names and their values for a specific object
     * @param obj the object to
     * @param <T>
     * @return returns key value pairs of the column names and their values for a specific object,
     * fields with null values will be ignored and none annotated ones
     */
    static <T> List<Pair<String, Object>> getFields(T obj) {

        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");

        return classInfoHashMap.get(obj.getClass().getName())
                .fields.entrySet().stream()
                .map((entry) -> convertToPairs(entry, obj))
                .filter(pair -> pair.second != null)
                .collect(Collectors.toList());
    }

    /**
     * converts the fields to key value pairs
     * @param entry map entry of column names and their corresponding fields
     * @param obj the object to get the values from
     * @return returns key value pairs of the column names and their values for a specific object
     */
    private static Pair<String, Object> convertToPairs(Map.Entry<String, Field> entry, Object obj) {
        Object value = runGetter(entry.getKey(), obj);

        if (entry.getValue().isEnumConstant() && value != null)
            value = value.toString();

        return new Pair<>(entry.getKey(), value);
    }
}
