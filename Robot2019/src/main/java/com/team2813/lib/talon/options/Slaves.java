package com.team2813.lib.talon.options;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Adrian Guerra
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Slaves {

	Slave[] value();
}
