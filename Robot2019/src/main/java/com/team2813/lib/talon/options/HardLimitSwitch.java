package com.team2813.lib.talon.options;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Sets up the Talon's hard limit switch config
 * @author Grady Whelan
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface HardLimitSwitch {

    LimitDirection direction();
    LimitSwitchNormal limitSwitchNormal() default LimitSwitchNormal.NormallyOpen;
    LimitSwitchSource limitSwitchSource() default LimitSwitchSource.FeedbackConnector;
    boolean enabled();
    boolean clearOnLimit();

    double paramValue() default 0;
    int paramSubValue() default 0;
    int paramOrdinal() default 0;

}