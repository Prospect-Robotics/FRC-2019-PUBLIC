package com.team2813.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team2813.lib.talon.BaseMotorControllerWrapper.PidIdx;
import com.team2813.lib.talon.CTREException;
import com.team2813.lib.talon.options.PIDProfile;
import com.team2813.lib.util.DriveSignal;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team2813.frc2019.subsystems.SubsystemMotorConfig.driveLeft;
import static com.team2813.frc2019.subsystems.SubsystemMotorConfig.driveRight;
import static com.team2813.lib.logging.LogLevel.DEBUG;
import static com.team2813.lib.talon.options.PIDProfile.Profile.PRIMARY;
import static com.team2813.lib.talon.options.PIDProfile.Profile.SECONDARY;

public class Drive extends Subsystem {

	private static final PIDProfile.Profile LOW_GEAR_VELOCITY_CONTROL_SLOT = PRIMARY;

	private static final PIDProfile.Profile HIGH_GEAR_VELOCITY_CONTROL_SLOT = SECONDARY;

	private static final int DRIVE_ENCODER_PPR = 4096;

	// Wheels Constants
	public static final double WHEEL_DIAMETER_INCHES = 6;
	public static final double WHEEL_RADIUS_INCHES = WHEEL_DIAMETER_INCHES / 2;
	public static final double WHEEL_TRACK_WIDTH_INCHES = 24; //TODO correct - this should be the space between the wheels (rn it's approx)
	public static final double TRACK_SCRUB_FACTOR = 1.0; // 254 code still says "tune me"

	private static final double LOW_GEAR_VELOCITY_KD = 10.0;//TODO tune
	
    private final Solenoid mShifter;
	// Control states	
	private DriveControlState mDriveControlState;

	public static final ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	// Hardware states
	private PeriodicIO mPeriodicIO;

	private boolean mIsBrakeMode;

	//#region Loops

	@Override
	public void onEnabledStart_(double timestamp) throws CTREException{
		//				setOpenLoop(new DriveSignal(0.05, 0.05));
		setBrakeMode(false);
		//                 startLogging();
	}

	@Override
	public void onEnabledLoop_(double timestamp) throws CTREException{
		// System.out.println("DRIVE CONTROL STATE: " + mDriveControlState);
		if (mDriveControlState == DriveControlState.OPEN_LOOP) {
		} else {
			System.out.println("Unexpected drive control state: " + mDriveControlState);
		}
	}

	@Override
	public void onEnabledStop_(double timestamp) throws CTREException {
		setOpenLoop(DriveSignal.NEUTRAL);
	}

	//#endregion

	//#region Network Tables

	private NetworkTableEntry targetingDataEntry;

	public class TargetingData{
		public double x, y, theta;
		private TargetingData(double x, double y, double theta) {
			this.x=x;
			this.y=y;
			this.theta=theta;
		}
	}

	public TargetingData targetingData;

	public void targetDataDeleteListener(EntryNotification notification){
		targetingData = null;
	}
	
	public void targetDataUpdateListener(EntryNotification notification){
		if(!notification.getEntry().exists()) return;
		double[] data = notification.getEntry().getDoubleArray((double[])null);
		if(data.length != 3) return;
		targetingData = new TargetingData(data[0], data[1], data[2]);
	}

	//#endregion
	
	Drive() {
		NetworkTable SmartDashboardTable = NetworkTableInstance.getDefault().getTable("SmartDashboard");
		NetworkTable targetingDataNetTable = SmartDashboardTable.getSubTable("vision");
		targetingDataEntry = targetingDataNetTable.getEntry("targetingData");
		targetingDataEntry.addListener(this::targetDataDeleteListener, EntryListenerFlags.kDelete);
		targetingDataEntry.addListener(this::targetDataUpdateListener, EntryListenerFlags.kUpdate);

		mPeriodicIO = new PeriodicIO();
        mShifter = new Solenoid(0,0);//PistonSolenoid(0);//makeSolenoidForId(0);//TODO magic number

		setOpenLoop(DriveSignal.NEUTRAL);

		// Force a CAN message across.
		mIsBrakeMode = false;
		setBrakeMode(true);
	}

	/**
	 * Configure talons for open loop control
	 */
	public synchronized void setOpenLoop(DriveSignal signal) {
		setDriveControlState(DriveControlState.OPEN_LOOP);
		mPeriodicIO.left_demand = signal.getLeft();
		mPeriodicIO.right_demand = signal.getRight();
		mPeriodicIO.left_feedforward = 0.0;
		mPeriodicIO.right_feedforward = 0.0;
	}

	/**
	 * Configures talons for velocity control
	 */
	public synchronized void setVelocity(DriveSignal signal, DriveSignal feedforward) {
		// setDriveControlState(DriveControlState.OPEN_LOOP);
		mPeriodicIO.left_demand = signal.getLeft();
		mPeriodicIO.right_demand = signal.getRight();
		mPeriodicIO.left_feedforward = feedforward.getLeft();
		mPeriodicIO.right_feedforward = feedforward.getRight();
	}
	
	public synchronized void setDriveControlState(DriveControlState newState) {
		if (newState != mDriveControlState) {
			if (newState == DriveControlState.OPEN_LOOP) {
				setBrakeMode(true);
				mPeriodicIO.left_demand = 0.0;
				mPeriodicIO.right_demand = 0.0;
			}
			mDriveControlState = newState;
		}
	}

	public synchronized void shiftGears() {
		setGear(!mShifter.get());
	}

	public synchronized void setGear(boolean wantsHighGear) {
		mShifter.set(wantsHighGear);
	}

	public boolean isBrakeMode() {
		return mIsBrakeMode;
	}

	public synchronized void setBrakeMode(boolean on) {
		if (mIsBrakeMode != on) {
			mIsBrakeMode = on;
			NeutralMode mode = on ? NeutralMode.Brake : NeutralMode.Coast;
			try {
				driveRight.setNeutralMode(mode);
				driveLeft.setNeutralMode(mode);
			} catch (CTREException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	protected void outputTelemetry_() throws CTREException {
		// SmartDashboard.putNumber("Right Drive Distance", mPeriodicIO.right_distance);
		// SmartDashboard.putNumber("Right Drive Ticks", mPeriodicIO.right_position_ticks);
		// SmartDashboard.putNumber("Left Drive Ticks", mPeriodicIO.left_position_ticks);
		// SmartDashboard.putNumber("Left Drive Distance", mPeriodicIO.left_distance);
		// SmartDashboard.putNumber("Right Linear Velocity", getRightLinearVelocity());
		// SmartDashboard.putNumber("Left Linear Velocity", getLeftLinearVelocity());

		// SmartDashboard.putNumber("x err", mPeriodicIO.error.getTranslation().x());
		// SmartDashboard.putNumber("y err", mPeriodicIO.error.getTranslation().y());
		// SmartDashboard.putNumber("theta err", mPeriodicIO.error.getRotation().getDegrees());
		SmartDashboard.putString("Drive Control State", ""+mDriveControlState);
		SmartDashboard.putBoolean("Gear", mShifter.get());
		SmartDashboard.putNumber("Gyro Raw Angle", gyro.getAngle());
	}

	public synchronized void resetEncoders() {
		try {
			driveLeft.setSelectedSensorPosition(PidIdx.PRIMARY_CLOSED_LOOP, 0);
			driveRight.setSelectedSensorPosition(PidIdx.PRIMARY_CLOSED_LOOP, 0);
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mPeriodicIO = new PeriodicIO();
	}

	@Override
	protected void zeroSensors_() {
		resetEncoders();
	}

	@Override
	protected synchronized void readPeriodicInputs_() {
		double prevLeftTicks = mPeriodicIO.left_position_ticks;
		double prevRightTicks = mPeriodicIO.right_position_ticks;
		try {
			mPeriodicIO.left_position_ticks = driveLeft.getSelectedSensorPosition();
			mPeriodicIO.right_position_ticks = driveRight.getSelectedSensorPosition();
			mPeriodicIO.left_velocity_ticks_per_100ms = driveLeft.getSelectedSensorVelocity();
			mPeriodicIO.right_velocity_ticks_per_100ms = driveRight.getSelectedSensorVelocity();
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double deltaLeftTicks = ((mPeriodicIO.left_position_ticks - prevLeftTicks) / 4096.0) * Math.PI;
		if (deltaLeftTicks > 0.0) {
			mPeriodicIO.left_distance += deltaLeftTicks * WHEEL_DIAMETER_INCHES;
		}
		else {
			mPeriodicIO.left_distance += deltaLeftTicks * WHEEL_DIAMETER_INCHES;
		}

		double deltaRightTicks = ((mPeriodicIO.right_position_ticks - prevRightTicks) / 4096.0) * Math.PI;
		if (deltaRightTicks > 0.0) {
			mPeriodicIO.right_distance += deltaRightTicks * WHEEL_DIAMETER_INCHES;
		}
		else {
			mPeriodicIO.right_distance += deltaRightTicks * WHEEL_DIAMETER_INCHES;
		}

		// System.out.println("control state: " + mDriveControlState + ", left: " + mPeriodicIO.left_demand + ", right: " + mPeriodicIO.right_demand);
	}

	@Override
	protected synchronized void writePeriodicOutputs_() throws CTREException {
		DEBUG.log(mDriveControlState);
		if (mDriveControlState == DriveControlState.OPEN_LOOP) {
			driveLeft.set(ControlMode.PercentOutput, mPeriodicIO.left_demand, DemandType.ArbitraryFeedForward, 0.0);
			driveRight.set(ControlMode.PercentOutput, mPeriodicIO.right_demand, DemandType.ArbitraryFeedForward, 0.0);
		}
		else {
			System.out.println("Auto mode???");
			driveLeft.set(
				ControlMode.Velocity, mPeriodicIO.left_demand * 0.5,
				DemandType.ArbitraryFeedForward, (
					mPeriodicIO.left_feedforward
					// + LOW_GEAR_VELOCITY_KD * mPeriodicIO.left_accel / 1023.0
				)
			);
			driveRight.set(
				ControlMode.Velocity, mPeriodicIO.right_demand * 0.5,
				DemandType.ArbitraryFeedForward, (
					mPeriodicIO.right_feedforward
					// + LOW_GEAR_VELOCITY_KD * mPeriodicIO.right_accel / 1023.0
				)
			);
			DEBUG.log("Left Demand:", mPeriodicIO.left_demand, "Left Feed Forward", mPeriodicIO.left_feedforward);
		}
	}

	@Override
	protected boolean checkSystem_() throws CTREException {
		//TODO check system
		return true;
	}

	// The robot drivetrain's various states.
	public enum DriveControlState {
		OPEN_LOOP, // open loop voltage control
	}

	public static class PeriodicIO {

		// INPUTS
		public int left_position_ticks;

		public int right_position_ticks;

		public double left_distance;

		public double right_distance;

		public int left_velocity_ticks_per_100ms;

		public int right_velocity_ticks_per_100ms;

		// OUTPUTS
		public double left_demand;

		public double right_demand;

		public double left_accel;

		public double right_accel;

		public double left_feedforward;

		public double right_feedforward;
	}
}
