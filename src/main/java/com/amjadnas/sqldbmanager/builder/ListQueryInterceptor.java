package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.exceptions.IllegalMethodException;
import com.amjadnas.sqldbmanager.exceptions.IllegalReturnTypeException;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


final class ListQueryInterceptor implements QueryInterceptor {

    private Class<?> returnType;
    private String query;

    /**
     * the constructor throws a IllegalMethodException if the return type of the method is not a List
     * of Entity annotated Objects
     *
     * @param returnType the return type of the method that is specified by the developer
     * @param query      sql query that entered by the developer
     */

    ListQueryInterceptor(Type returnType, String query) {
        ParameterizedType pt = (ParameterizedType) returnType;
        this.returnType = (Class) pt.getActualTypeArguments()[0];
        if (!AnnotationProcessor.isEntity(this.returnType))
            throw new IllegalReturnTypeException("Only list of Entities are supported as a reurn type");
        this.query = query;
    }

    /**
     * this method deals with queries that returns a list of objects from database
     * the only supported return type is a java.lang.List of the Entity annotated objects
     *
     * @param connection from the database to process the request
     * @param whereArgs  "where" argument values
     * @return an object of the entity or byte array depends on what the developers specifies in the return type
     * @throws NoSuchMethodException     if the required constructor is not defined
     * @throws InstantiationException    if the constructor invocation failed
     * @throws SQLException              if there were sql related errors
     * @throws IllegalAccessException    if the requested constructor was private
     * @throws InvocationTargetException failed to invoke target
     * @throws ClassNotFoundException    if the class wasn't registered in the JVM
     */
    @Override
    public List<Object> intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        List<Object> list = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int bound = whereArgs.length;
            for (int i = 0; i < bound; i++) {
                preparedStatement.setObject(i + 1, whereArgs[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Object obj = ConstructorUtils.invokeConstructor(returnType);
                    for (int i = 1; i <= colCount; i++) {
                        String className = metaData.getColumnClassName(i);
                        String columnName = metaData.getColumnName(i);
                        ClassHelper2.runSetter(columnName, obj, resultSet.getObject(i, Class.forName(className)));
                    }
                    list.add(obj);
                }
            }
            return list;
        }
    }

    /**
     * this method is not used if the developer tries to invoke the method,
     * it will throw an IllegalMethodException
     *
     * @param connection connection form the database
     * @param object     object to be processed
     * @return single selected object from database
     */
    @Override
    public Object intercept2(Connection connection, Object object) {
        throw new IllegalMethodException("Wrong method signature.  method arguments must be (Connection, Object[]) not (Connection, Object)");
    }
}
