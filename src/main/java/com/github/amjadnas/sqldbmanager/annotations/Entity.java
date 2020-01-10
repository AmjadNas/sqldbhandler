package com.github.amjadnas.sqldbmanager.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that this is an entity stored in the database.
 * the entity name must be provided and it must be the same as its name in the database(name)
 * primaryKey must be provided whether it is a one column or more(primaryKey),
 * the last configuration is to specify whether the primary key of the table is auto incremented by
 * the database upon insertion this configuration is optional and false by default(isAutoIncrement)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    /**
     *
     * @return entity name represented in the database
     */
    String name();

    /**
     *
     * @return array of the primary keys
     */
    String[] primaryKey();

    /**
     *
     * @return whether is the primary key of the entity is auto incremented by the database
     */
    boolean isAutoIncrement() default false;

}
