package com.team2813.lib.talon.options;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Adrian Guerra
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface PIDProfiles {
	PIDProfile[] value();
}
