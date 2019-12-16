package com.github.amjadnas.sqldbmanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation indicates that the method performs a SELECTION query
 * the method signature must be MyObject/List<MyObject> "methodName"(Connection, Object)
 * be aware that if the return type was a single object and the query returns a list
 * then the method will return the first object from the selected rows
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value();


}
