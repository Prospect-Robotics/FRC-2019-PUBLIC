package com.team2813.frc2019;


import com.team2813.lib.util.DriveSignal;

/**
 * 
 * Helper class for driving in teleop
 * 
 */
public class TeleopDriveSignalGenerator {

    private static final double kThrottleDeadband = 0.02;
    private static final double kWheelDeadband = 0.02;

    public DriveSignal arcadeDrive(double throttle, double turn, boolean fineAdjustment) {

        turn = handleDeadband(turn, kWheelDeadband) * (!fineAdjustment ? 0.4 : 1.0);
        throttle = handleDeadband(throttle, kThrottleDeadband);

        double leftPwm = throttle - turn * Math.abs(turn);
		double rightPwm = throttle + turn * Math.abs(turn);
        
        if (fineAdjustment) {
            leftPwm = throttle - turn;
            rightPwm = throttle + turn;
            return new DriveSignal(leftPwm, rightPwm);
        }

    	if (Math.abs(turn) < 0.15) {
    		leftPwm = throttle;
    		rightPwm = throttle;
        }
        
        if (Math.abs(throttle) < 0.15) {
            leftPwm = 0;
            rightPwm = 0;
        }

        if (leftPwm == 0 && rightPwm == 0 && turn != 0) {
            leftPwm = -turn * Math.abs(turn);
            rightPwm = turn * Math.abs(turn);
        }

        return new DriveSignal(leftPwm, rightPwm);
    }

    private double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }
}
