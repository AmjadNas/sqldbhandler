package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.annotations.Delete;
import com.amjadnas.sqldbmanager.annotations.Query;
import com.amjadnas.sqldbmanager.annotations.Update;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

final class DaoBuilder {

     private DaoBuilder(){}

     static <T> T buildDao(Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //TODO refactor if statements
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
                        .intercept(MethodDelegation.to(new InsertInterceptor(method.getReturnType()), QueryInterceptor.class));
            }
            else if (AnnotationProcessor.isUpdate(method)){
                String[] keys = method.getAnnotation(Update.class).updateBy();
                builder = builder
                        .method(ElementMatchers.named(method.getName())
                                .and(ElementMatchers.takesArguments(method.getParameterTypes())))
                        .intercept(MethodDelegation.to(new UpdateInterceptor(method.getReturnType(), keys), QueryInterceptor.class));
            }
            else if (AnnotationProcessor.isDelete(method)){
                String[] keys = method.getAnnotation(Delete.class).deleteBy();
                builder = builder
                        .method(ElementMatchers.named(method.getName())
                                .and(ElementMatchers.takesArguments(method.getParameterTypes())))
                        .intercept(MethodDelegation.to(new DeleteInterceptor(method.getReturnType(), keys), QueryInterceptor.class));
            }
        }


        Class<?> dynamicType = builder.make()
                .load(clazz.getClassLoader())
                .getLoaded();
         Stream.of(dynamicType.getDeclaredMethods()).forEach(System.out::println);
        return (T) ConstructorUtils.invokeConstructor(dynamicType);
    }
}
