package com.github.amjadnas.sqldbmanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The delete annotation is used to indicate that a method will perform
 * delete operation on the database.
 * <ol>
 * This annotation can be configured in two ways:
 * <li>
 * <p>
 * *  *  * annotate the method without providing "where arguments"(deleteBy)to perform the delete operation by,
 * *  *  *    which will result in deleting the the in the database based on the provided primaryKey.
 * *  *  *    in this approach the method must be void/MyObject/int "methodName"(Connection, Object),
 * *  *  *    where the return tyoe determines if the method returns nothing(void), number of affected rows(int)
 * *  *  *    , deleted object(MyObject).
 * </p>
 * </li>
 * <li>
 * annotate the method and provide the deleteBy value. In ths case the method must be provided as the
 * *   following void/int "methodName"(Connection, Object[]), where the first object in
 * *   the array of objects is the object to be deleted and the rest is the values of the where arguments
 * *   if the objects where not set then the database might throw a SQLException or the library will ignore
 * *   the deleteBy values, it depends on wether the user overloads the method with a differant configuration
 * *   for Example:
 * *
 * *      <p>
 * *  *     @Delete(deleteBy = {"name"})  // deleteBy values ignored
 * *  *     Movie delete(Connection connection, Movie move);
 * *  *
 * *  *     @Delete
 * *  *    int delete(Connection connection, Object...whargs); // SQLException thrown
 * *      </p>
 * </li>
 * <ol/>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {
    /**
     *
     * @return the where arguments to delete the rows by
     */
    String[] deleteBy() default {};
}
