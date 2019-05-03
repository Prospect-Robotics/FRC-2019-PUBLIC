package com.team2813.lib.talon.options;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ctre.phoenix.motorcontrol.InvertType;

/**
 * @author Adrian Guerra
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(Slaves.class)
public @interface Slave {

	int id();

	InvertType followMode() default InvertType.FollowMaster;
	
	MotorControllerType motorControllerType() default MotorControllerType.VICTOR;
	
	public enum MotorControllerType{
		TALON, VICTOR;
	}
}
