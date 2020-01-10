package com.github.amjadnas.sqldbmanager.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column is an annotation for a field that corresponds with a column in a table
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /***
     * the name of the column as it is stored in the database
     * this field is mandatory because it helps the library to
     * identify the field and it's relative column in
     * the database this column name must be provided exactly how to
     * it's spelled in the database
     * @return a string that represents the name of the column
     */
    String name();

}
