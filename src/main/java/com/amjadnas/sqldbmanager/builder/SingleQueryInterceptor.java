package com.amjadnas.sqldbmanager.builder;

import com.amjadnas.sqldbmanager.exceptions.IllegalReturnTypeException;
import com.amjadnas.sqldbmanager.utills.AnnotationProcessor;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * A class that deals with queries that outputs a single object
 */
final class SingleQueryInterceptor implements QueryInterceptor {
    private Class<?> returnType;
    private String query;
    private boolean isByteArray;

    /**
     * the constructor throws a IllegalMethodException if the return type of the method is not an
     * Entity annotated Object or a byte array (byte[])
     *
     * @param returnType the return type of the method that is specified by the developer
     * @param query      sql query that entered by the developer
     */
    SingleQueryInterceptor(Class<?> returnType, String query) {
        this.returnType = returnType;
        if (!AnnotationProcessor.isEntity(this.returnType) && !returnType.isAssignableFrom(byte[].class))
            throw new IllegalReturnTypeException("Only Entities or byte array (byte[]) are supported as a return type");
        this.query = query;
    }

    /**
     * this method deals with queries that returns a single object from database
     * so if the the query was defined to return more than one object and the returned type was an Object
     * and not List<Object>> then the first row of the selected data will be converted to an object and returned
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
    public Object intercept(Connection connection, Object... whereArgs) throws NoSuchMethodException, InstantiationException, SQLException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Object obj = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int bound = whereArgs.length;
            for (int i = 0; i < bound; i++) {
                preparedStatement.setObject(i + 1, whereArgs[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                if (resultSet.next()) {
                    if (returnType.isAssignableFrom(byte[].class))
                        obj = resultSet.getBytes(1);
                    else {
                        obj = ConstructorUtils.invokeConstructor(returnType);
                        for (int i = 1; i <= colCount; i++) {
                            String className = metaData.getColumnClassName(i);
                            String columnName = metaData.getColumnName(i);

                            ClassHelper2.runSetter(columnName, obj, resultSet.getObject(i, Class.forName(className)));
                        }
                    }
                }
            }
        }
        return obj;
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
        throw new RuntimeException("Wrong method signature.  method arguments must be (Connection, Object[]) not (Connection, Object)");
    }
}
