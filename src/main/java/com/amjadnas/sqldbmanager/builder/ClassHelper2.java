package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Column;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import com.amjadnas.sqldbmanager.utills.Pair;
import test.Category;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassHelper2 {

    private static ClassHelper2 instance;
    private static final Map<String, ClassInfo> classInfoHashMap = new HashMap<>();

    private ClassHelper2() {

    }

    public static void addClass(Class clazz) {
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

    public static ClassHelper2 getInstance() {
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

    private static MethodHandle findMethod(String type, Field field, Class<?> clazz) {
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        try {
           // String methodName = "";
            for (Method method : clazz.getDeclaredMethods()) {
                if ((method.getName().startsWith(type)) && (method.getName().length() == (field.getName().length() + 3))) {
                    if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {

                        return  publicLookup.unreflect(method);

                    }
                }
            }

          /*  MethodType mt;
            if (type.equals("get")) {
                mt = MethodType.methodType(field.getType());
                return publicLookup.findVirtual(clazz, methodName, mt);

            } else if (type.equals("set")) {
                mt = MethodType.methodType(void.class, field.getType());
                return publicLookup.findVirtual(clazz, methodName, mt);
            }*/

        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
            ClassInfo classInfo = classInfoHashMap.get(className);
            Field field = classInfo.fields.get(columnName);
            Class clazz = field.getType();
            if (clazz.isEnum()) {

                // value = Enum.valueOf(clazz, value.toString());
                value = null;
            }


             classInfo.setters.get(columnName).invoke(o,value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Could not determine method for column: " + columnName);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }


    public static <T> Object runGetter(String columnName, T o) {
        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");
        try {

            String className = o.getClass().getName();
            ClassInfo classInfo = classInfoHashMap.get(className);
            Object object = classInfo.getters.get(columnName).invokeExact();
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


    public static <T> List<Pair<String, Object>> getFields(T obj) {

        if (instance == null)
            throw new IllegalStateException("ClassHelper is not initialized!");

        return classInfoHashMap.get(obj.getClass().getName())
                .fields.entrySet().stream()
                .map((entry) -> convertToPairs(entry, obj))
                .filter(pair -> pair.second != null)
                .collect(Collectors.toList());
    }

    private static Pair<String, Object> convertToPairs(Map.Entry<String, Field> entry, Object obj) {
        Object value = runGetter(entry.getKey(), obj);

        if (entry.getValue().isEnumConstant() && value != null)
            value = value.toString();

        return new Pair<>(entry.getKey(), value);
    }
}
