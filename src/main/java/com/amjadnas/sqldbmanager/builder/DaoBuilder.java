package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Query;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;

final class DaoBuilder {

     private DaoBuilder(){}

     static <T> T buildDao(Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        DynamicType.Builder builder = new ByteBuddy()
                .subclass(clazz)
                .name(clazz.getPackage().getName() + "." + clazz.getSimpleName() + "Impl");
        for (Method method : clazz.getDeclaredMethods()) {
            Type returnType = method.getGenericReturnType();
            String query = method.getAnnotation(Query.class).value();
           if (returnType instanceof Class){
               builder = builder
                       .method(ElementMatchers.named(method.getName()).and(ElementMatchers.takesArguments(Connection.class, Object[].class)))
                       .intercept(MethodDelegation.to(new SingleInterceptor((Class)returnType, query), Interceptor.class));

           }else {
               builder = builder
                       .method(ElementMatchers.named(method.getName()).and(ElementMatchers.takesArguments(Connection.class, Object[].class)))
                       .intercept(MethodDelegation.to(new ListInterceptor(returnType, query), Interceptor.class));


           }

        }
        Class<?> dynamicType = builder.make()
                .load(clazz.getClassLoader())
                .getLoaded();
        return (T) ConstructorUtils.invokeConstructor(dynamicType);
    }
}
