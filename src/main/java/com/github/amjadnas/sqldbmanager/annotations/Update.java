package com.github.amjadnas.sqldbmanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * The update annotation is used to indicate that a method will perform
 * update operation on the database.
 * <br>
 * This annotation can be configured in two ways:
 * <ul>
 * <li>
 * <p>
 *  annotate the method without providing "where arguments"(UpdateBy)to perform the Update operation by,
 *  which will result in deleting the the in the database based on the provided primaryKey.
 *  in this approach the method must be void/MyObject/int "methodName"(Connection, Object),
 *  where the return type determines if the method returns nothing(void), number of affected rows(int)
 * , Updated object(MyObject).
 * </p>
 * </li>
 * <li>
 * annotate the method and provide the UpdateBy value. In ths case the method must be provided as the
 *    following void/int "methodName"(Connection, Object[]), where the first object in
 *    the array of objects is the object to be Updated and the rest is the values of the where arguments
 *    if the objects where not set then the database might throw a SQLException or the library will ignore
 *    the updateBy values, it depends on whether the user overloads the method with a different configuration
 *    for Example:
 *
 *      <p>
 *      *@Update(updateBy = {"name"})  // deleteBy values ignored
 *       Movie delete(Connection connection, Movie move);
 *
 *      *@Update
 *      int delete(Connection connection, Object...whargs); // SQLException thrown
 *        </p>
 * </li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {
    /**
     *
     * @return the where arguments to update the rows by
     */
    String[] updateBy() default {};
}
