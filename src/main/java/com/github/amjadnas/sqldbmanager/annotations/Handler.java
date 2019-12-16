package com.github.amjadnas.sqldbmanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation indicates the class is the access point for the DAOs
 * the annotated class must be instantiated by calling DBHandler.build()
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    /**
     *
     * @return clases of the entities to be prepossessed
     */
    Class[] entities();

}
