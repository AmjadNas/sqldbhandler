package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Query;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import test.Movie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.stream.Stream;


final class DaoBuilder {

     private DaoBuilder(){}

     static <T> T buildDao(Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        DynamicType.Builder builder = new ByteBuddy()
                .subclass(clazz)
                .name(clazz.getPackage().getName() + "." + clazz.getSimpleName() + "Impl");
        for (Method method : clazz.getDeclaredMethods()) {
            Type returnType = method.getGenericReturnType();
            if (AnnotationProcessor.isQuery(method)) {
                String query = method.getAnnotation(Query.class).value();
                if (returnType instanceof Class) {
                    builder = builder
                            .method(ElementMatchers.named(method.getName())
                                    .and(ElementMatchers.takesArguments(method.getParameterTypes())))
                            .intercept(MethodDelegation.to(new SingleQueryInterceptor((Class) returnType, query), QueryInterceptor.class));
                } else {
                    builder = builder
                            .method(ElementMatchers.named(method.getName())
                                    .and(ElementMatchers.takesArguments(method.getParameterTypes())))
                            .intercept(MethodDelegation.to(new ListQueryInterceptor(returnType, query), QueryInterceptor.class));
                }
            }else if (AnnotationProcessor.isInsert(method)){
                builder = builder
                        .method(ElementMatchers.named(method.getName())
                                .and(ElementMatchers.takesArguments(method.getParameterTypes())))
                        .intercept(MethodDelegation.to(new InsertInterceptor(), QueryInterceptor.class));
            }
        }
        Class<?> dynamicType = builder.make()
                .load(clazz.getClassLoader())
                .getLoaded();
         Stream.of(dynamicType.getDeclaredMethods()).forEach(System.out::println);
        return (T) ConstructorUtils.invokeConstructor(dynamicType);
    }
}
