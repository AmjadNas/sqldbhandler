package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.annotations.Query;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DaoBuilder {

    public static <T> T buildDao(Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> dynamicType;


        DynamicType.Builder builder = new ByteBuddy()
                .subclass(clazz)
                .name(clazz.getPackage().getName()+"."+clazz.getSimpleName()+"Impl");


        for (Method method : clazz.getDeclaredMethods()) {

            builder = builder
                   .method(ElementMatchers.named(method.getName()).and(ElementMatchers.takesArguments(Connection.class, Object[].class)))
                    //.method(any())
                    .intercept(MethodDelegation.to(new Foo() {

                        public Object intercept (Connection connection, Object...args) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
                            String query = method.getAnnotation(Query.class).value();
                           /* Type returnType = method.getGenericReturnType();
                            if (returnType instanceof ParameterizedType){
                                ParameterizedType type = (ParameterizedType)returnType;
                                Class c = (Class)type.getActualTypeArguments()[0];
                                QueryHandler handler = FactoryQueryHandler.getHandler("list");
                                return handler.handleQuery(connection, query, c, args);
                            }*/
                           System.out.println(method.getGenericReturnType());
                           return new ArrayList<>();
                        }
                    }, Foo.class));

        }



        dynamicType = builder.make()
                .load(clazz.getClassLoader())
                .getLoaded();

        return (T) ConstructorUtils.invokeConstructor(dynamicType);
    }
}
