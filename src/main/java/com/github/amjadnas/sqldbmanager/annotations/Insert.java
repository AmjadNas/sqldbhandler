package com.github.amjadnas.sqldbmanager.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The annotation indicates the the given method is a method that performs an insert operation
 * the method signture must be void/int/MyObject "methodName"(Connection, Object)
 * where return type is if it's void then it returns nothing
 * if it's MyObject it returns the inserted object with the auto generated id (if the primary key is auto increment)
 * else if int then it would return the generated id or the number of affected rows
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Insert {
}
