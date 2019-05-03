/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2813.frc2019;

import com.team2813.frc2019.loops.Loop;
import com.team2813.frc2019.subsystems.Climber;
import com.team2813.frc2019.subsystems.Subsystem;
import com.team2813.frc2019.subsystems.Subsystems;
import com.team2813.lib.logging.Logger;
import com.team2813.lib.solenoid.PistonSolenoid.PistonState;
import com.team2813.lib.talon.CTREException;
import com.team2813.lib.util.CrashTracker;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team2813.frc2019.ControlBoard.*;
import static com.team2813.frc2019.subsystems.Subsystems.*;
import static com.team2813.lib.logging.LogLevel.DEBUG;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
    public static TeleopDriveSignalGenerator driveSignalGenerator = new TeleopDriveSignalGenerator();

	private static final boolean ENABLE_CLIMB = true;
    public static boolean isAuto = false;

    private boolean usingFineAdjustment = false;
    public static boolean isClimbing = false;
    public static boolean climbInitiated = false;
	public static boolean autoElevator = false;

	public static boolean driveAngleInProgress = false;

    private static NetworkTableEntry shutdownEntry = null;

    {
        for (Loop subsystem : allSubsystems) {
            LOOPER.addLoop(subsystem);
        }
    }

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
    	try {

            NetworkTable SmartDashboardTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");
            NetworkTable visionSubTable = SmartDashboardTable.getSubTable("vision");
            shutdownEntry = visionSubTable.getEntry("shutdown");
            shutdownEntry.setBoolean(false);

            CrashTracker.logRobotInit();

        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void disabledInit() {

        SmartDashboard.putString("Match Cycle", "DISABLED");

        try {

        	CrashTracker.logDisabledInit();
            LOOPER.setMode(RobotMode.DISABLED);
            LOOPER.start();

        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    /**
     * Called at the beginning of sandstorm
     */
    @Override
    public void autonomousInit() {
		SmartDashboard.putString("Match Cycle", "SANDSTORM");

		try {
			CrashTracker.logAutoInit();

			HATCH.setClamp(PistonState.EXTENDED);

			DRIVE.setGear(false);

			CLIMBER.master.setSelectedSensorPosition(CLIMBER.master.getSelectedSensorPosition());

			ELEVATOR.zeroSensors();
			HATCH.zeroSensors();
			DRIVE.zeroSensors();
			CLIMBER.zeroSensors();

			CLIMBER.setNextPositionAndMove(Climber.Position.HOME);

			climbInitiated = false;

			teleopInit();
		} catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
        }
    }

    /**
     * Called at the beginning of sandstorm and teleop
     */
    @Override
    public void teleopInit() {
        if (!DriverStation.getInstance().isAutonomous()) SmartDashboard.putString("Match Cycle", "TELEOP");

        try {
            CrashTracker.logTeleopInit();
            LOOPER.setMode(RobotMode.ENABLED);
            LOOPER.start();

			HATCH.master.setClosedLoopRamp(0.001); // FIXME is this needed / change constant

        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            try {
                throw t;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void testInit() {
        SmartDashboard.putString("Match Cycle", "TEST");

        try {
            System.out.println("Starting check systems.");

            // TODO what mode should looper be in here
            // disabledLooper.stop();
            // enabledLooper.stop();

            // mDrive.checkSystem();
            // mIntake.checkSystem();
            // mWrist.checkSystem();
            // mElevator.checkSystem();
            Logger.logLevel = DEBUG;
            autonomousInit();

        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void disabledPeriodic() {
        SmartDashboard.putString("Match Cycle", "DISABLED");

        if (RobotController.getUserButton()) {
            shutdownEntry.setBoolean(true);
        }

        try {
            ELEVATOR.resetIfAtLimit();
            HATCH.resetIfAtLimit();
            CLIMBER.resetIfAtLimit();
        } catch (CTREException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called periodically during sandstorm.
     */
    @Override
    public void autonomousPeriodic() {
        SmartDashboard.putString("Match Cycle", "SANDSTORM");

        teleopPeriodic();
    }

    /**
     * This function is called periodically during teleop.
     */
    @Override
    public void teleopPeriodic() {
        if (!DriverStation.getInstance().isAutonomous()) SmartDashboard.putString("Match Cycle", "TELEOP");
        double timestamp = Timer.getFPGATimestamp();

        double throttle = Axis.THROTTLE.get();
        double turn = Axis.TURN.get();

        if (isClimbing) {
			isAuto = false;
        	throttle = 0;
        	turn = 0;
		}

        System.out.println(
            "Throttle" + throttle +
            "Turn" + turn
        );

        if (throttle < 0.15 && turn < 0.15) {

			if (POV.FORWARD.inAngle()) {
				throttle = 0.2;
				turn = 0.0;
			} else if (POV.LEFT.inAngle()) {
				turn = -0.2;
				throttle = 0.0;
			} else if (POV.REVERSE.inAngle()) {
				throttle = -0.2;
				turn = 0.0;
			} else if (POV.RIGHT.inAngle()) {
				turn = 0.2;
				throttle = 0.0;
			}
			usingFineAdjustment = !POV.NEUTRAL.inAngle();
		}

        try {

			if (DRIVE.targetingData == null) DEBUG.log("Targeting Data Null");
			else DEBUG.log(
				"Targeting Data X: ", DRIVE.targetingData.x,
				"; Y: ", DRIVE.targetingData.y,
				"; Theta: ", DRIVE.targetingData.theta
			);
            ELEVATOR.resetIfAtLimit();
			HATCH.resetIfAtLimit();
//			CLIMBER.resetIfAtLimit();
			
            if (!isAuto) {
                DRIVE.setOpenLoop(
                    driveSignalGenerator.arcadeDrive(throttle, turn, usingFineAdjustment));
                Button.SHIFT_GEARS.whenPressed(DRIVE::shiftGears);
            }

			if (ENABLE_CLIMB) {

				if (isClimbing) {
					DEBUG.log("isClimbing");
					Button.ABORT.whenPressed(CLIMBER::abort);
					Button.RETRACT_EXDENDY.whenPressed(CLIMBER::retractExdendy);
					POV.CLIMB_WHEEL.whileInAngle(CLIMBER::setWheelMoving);
					POV.NEUTRAL.whileInAngle(CLIMBER::setWheelStopped);
					return;//Return will prevent unintentional operation of elevator/hatch in climb
				}

				Button.LEVEL_2_CLIMB.whenPressed(() -> CLIMBER.climb(Climber.Movement.TWO));
				Button.LEVEL_3_CLIMB.whenPressed(() -> CLIMBER.climb(Climber.Movement.THREE));
			}

			Button.HATCH_OPEN_LOOP_UP.whenPressed(HATCH::openLoopUpStart);
			Button.HATCH_OPEN_LOOP_UP.whenReleased(HATCH::openLoopUpStop);

			// TODO need to test on comp bot as this might not work - practice bot has no limit switch
			Button.SLOWLY_FLOOR_ELEVATOR.whenPressed(ELEVATOR::slowlyFloor);

			// ELEVATOR TELEOP
			if (!autoElevator) {
				Button.ELEVATOR_CLOCKWISE.whenPressed(() -> ELEVATOR.setNextPosition(true));
				Button.ELEVATOR_COUNTERCLOCKWISE.whenPressed(() -> ELEVATOR.setNextPosition(false));
			}

			Button.FRONT_PISTONS.whenPressed(HATCH::autoPistons);
			Button.CLAMP.whenPressed(HATCH::toggleClamp);

			Button.HATCH_LOAD_WITH_CARGO.whenPressed(() -> {
				HATCH.setNextPosition(true);
				HATCH.loadWithCargo();
			});

			outputToSmartDashboard();
		} catch (CTREException e) {
			e.printStackTrace();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
        autonomousPeriodic();
    }

    PowerDistributionPanel pdp = new PowerDistributionPanel();

    private void outputToSmartDashboard() {
        LOOPER.outputToSmartDashboard();
        SmartDashboard.putBoolean("Auto in Progress", isAuto);
        SmartDashboard.putBoolean("Drive Angle in Progress", driveAngleInProgress);
		SmartDashboard.putBoolean("Center/Outer", HATCH.getFrontPistons());
		SmartDashboard.putBoolean("Clamp", HATCH.getClamp());
		SmartDashboard.putBoolean("Climb Initiated", climbInitiated);

		SmartDashboard.putData("PDP", pdp);
//		Subsystems.POWER_MANAGEMENT.putToDashboard();

        // SmartDashboard.putNumber("X Target", DRIVE.targetingData.x);
        // SmartDashboard.putNumber("Y Target", DRIVE.targetingData.y);
        // SmartDashboard.putNumber("Theta Target", DRIVE.targetingData.theta);

        for (Subsystem subsystem : allSubsystems)
            if (subsystem == Subsystems.DRIVE)
				subsystem.outputTelemetry();
    }

	public enum RobotMode {
		DISABLED, ENABLED
	}

}