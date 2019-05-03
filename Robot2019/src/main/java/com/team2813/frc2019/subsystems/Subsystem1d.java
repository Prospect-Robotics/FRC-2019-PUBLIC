package com.team2813.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.team2813.lib.talon.BaseMotorControllerWrapper.PidIdx;
import com.team2813.lib.talon.CTREException;
import com.team2813.lib.talon.TalonWrapper;
import com.team2813.lib.talon.options.PIDProfile;
import com.team2813.lib.talon.options.PIDProfile.Profile;
import com.team2813.lib.util.Util;
import edu.wpi.first.wpilibj.Timer;

import static com.team2813.lib.logging.LogLevel.DEBUG;

/**
 * 
 */
abstract class Subsystem1d<P extends Subsystem1d.Position<P>> extends Subsystem {

	public TalonWrapper master;

	PeriodicIO mPeriodicIO = new PeriodicIO();

	private boolean mHasBeenZeroed = false;

	private ZeroingMode zeroingMode;

	Mode currentMode = Mode.HOLDING;

	Subsystem1d(TalonWrapper master, ZeroingMode zeroingMode) {

		try {
			this.master = master;

			this.zeroingMode = zeroingMode;

			master.enableCurrentLimit();

			// master.setParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0);
			// master.setParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0);

			// TODO add low gear gains

			master.selectProfileSlot(Profile.PRIMARY, PidIdx.PRIMARY_CLOSED_LOOP);

			//master.enableLimitSwitches();
			//master.disableOverrideSoftLimits();

			master.enableVoltageCompensation();

			master.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10);// FIXME should this (periodMs) be
																					// 20?
			master.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10);// FIXME should this (periodMs)
																						// be
																						// 20?

			//master.setSensorPhaseInverted(true);

			// Start with zero power.
			master.set(ControlMode.PercentOutput, 0);
			master.setNeutralMode(NeutralMode.Brake);
//			setPosition(master.getSelectedSensorPosition());
//			setMode(Mode.HOLDING);
		} catch (CTREException e) {
			new CTREException("Subsystem construction failed", e).printStackTrace();
			constructorFailed();
		}
	}

	private synchronized void setPosition(int encoderPosition) {
		mPeriodicIO.demand = encoderPosition;
	}

	synchronized void setPosition(P position) {
		setPosition(position.getPos());
	}

	public synchronized void setPositionAndMove(P position) {
		setPosition(position);
		setMode(Mode.MOVING);
	}

	public synchronized void setPositionWithMode(P position, Mode mode) {
		setPosition(position);
		setMode(mode);
	}

	public abstract void setNextPosition(boolean clockwise);

	public synchronized boolean hasFinishedTrajectory() {
		return currentMode.state == SubsystemControlState.MOTION_MAGIC
				&& Util.epsilonEquals(mPeriodicIO.active_trajectory_position, mPeriodicIO.demand, 5);
	}

	public synchronized double getRPM() {
		// We are using a CTRE mag encoder which is 4096 native units per revolution.
		return mPeriodicIO.velocity_ticks_per_100ms * 10.0 / 4096.0 * 60.0;
	}

	public synchronized double getSetpoint() {
		return currentMode.state == SubsystemControlState.MOTION_MAGIC ? mPeriodicIO.demand : Double.NaN;
	}

	public synchronized double getActiveTrajectoryAccelG() {

		return mPeriodicIO.active_trajectory_accel_g;
	}

	@Override
	public void outputTelemetry_() {

	}

	public void resetPIDF(double p, double i, double d, double f) {
		try {
			master.setPIDF(PIDProfile.Profile.PRIMARY, p, i, d, f);
		} catch (CTREException e) {
			e.printStackTrace();
		}
	}

	// #region Looping

	@Override
	protected void onEnabledStart_(double timestamp) throws CTREException {
		// TODO
	}

	@Override
	protected void onEnabledLoop_(double timestamp) throws CTREException {
		// TODO
	}

	@Override
	protected void onEnabledStop_(double timestamp) throws CTREException {
		master.set(ControlMode.PercentOutput, 0.0);
	}

	// #endregion

	@Override
	public synchronized void zeroSensors_() {

		try {
			master.setSelectedSensorPosition(0);
			master.setSelectedSensorPosition(PidIdx.PRIMARY_CLOSED_LOOP, 0);
			DEBUG.log(master.subsystemName, "zeroed 1", master.getSelectedSensorPosition());
		} catch (CTREException e) {
			e.printStackTrace();
		}
		mHasBeenZeroed = true;
	}

	public synchronized boolean hasBeenZeroed() {
		return mHasBeenZeroed;
	}

	public synchronized void resetIfAtLimit() throws CTREException {
		if (mPeriodicIO.limit_switch) {
			zeroSensors_();
		}
	}

	// TODO 2813 Rewrite more logically
	@Override
	public synchronized void readPeriodicInputs_() {
		final double t = Timer.getFPGATimestamp();
		try {
			mPeriodicIO.position_ticks = master.getSelectedSensorPosition(PidIdx.PRIMARY_CLOSED_LOOP);
			mPeriodicIO.velocity_ticks_per_100ms = master.getSelectedSensorVelocity(PidIdx.PRIMARY_CLOSED_LOOP);
			if (master.getControlMode() == ControlMode.MotionMagic) {
				mPeriodicIO.active_trajectory_position = master.getActiveTrajectoryPosition();
				// TODO check sign of elevator accel
				mPeriodicIO.active_trajectory_velocity = master.getActiveTrajectoryVelocity();
			} else {
				mPeriodicIO.active_trajectory_position = Integer.MIN_VALUE;
				mPeriodicIO.active_trajectory_velocity = 0;
			}
			mPeriodicIO.output_percent = master.getMotorOutputPercent();

			if (zeroingMode.forward)
				mPeriodicIO.limit_switch = master.getSensorCollection().isFwdLimitSwitchClosed();
			if (zeroingMode.reverse)
				mPeriodicIO.limit_switch = master.getSensorCollection().isRevLimitSwitchClosed();
		} catch (CTREException e) {
			e.printStackTrace();
		}
		mPeriodicIO.t = t;
	}

	@Override
	public synchronized void writePeriodicOutputs_() {
		try {
			double demand = mPeriodicIO.demand;
			DEBUG.log(master.subsystemName, currentMode.state.mode, demand, master.getSelectedSensorPosition());
			master.set(currentMode.state.mode, demand);
		}
		catch (CTREException e) {
			e.printStackTrace();
		}
		try {
			resetIfAtLimit();
		}
		catch (CTREException e) {
			e.printStackTrace();
		}
	}

	protected enum SubsystemControlState {
		MOTION_MAGIC(ControlMode.MotionMagic), POSITION_PID(ControlMode.MotionMagic);

		private ControlMode mode;

		SubsystemControlState(ControlMode mode) {
			this.mode = mode;
		}
	}

	public static class PeriodicIO {

		// INPUTS
		int position_ticks;

		int velocity_ticks_per_100ms;

		double active_trajectory_accel_g;

		int active_trajectory_velocity;

		int active_trajectory_position;

		double output_percent;

		boolean limit_switch;

		public double t;

		// OUTPUTS
		double demand;
	}

	// TODO document
	protected interface Position<E> {
		/** int encoder ticks */
		int getPos();

		E getNextClockwise();

		E getNextCounter();

		E getMin();

		E getMax();

		default E getClock(boolean clockwise) {
			return clockwise ? getNextClockwise() : getNextCounter();
		}
	}

	//#region Mode

	public void setMode(Mode mode) {
		master.selectProfileSlot(mode.profile, mode.pidIdx);
		currentMode = mode;
	}

	public enum Mode {
		MOVING(Subsystem1d.SubsystemControlState.MOTION_MAGIC, PIDProfile.Profile.PRIMARY,
				TalonWrapper.PidIdx.PRIMARY_CLOSED_LOOP),
		HOLDING(Subsystem1d.SubsystemControlState.POSITION_PID, PIDProfile.Profile.SECONDARY,
				TalonWrapper.PidIdx.AUXILIARY_CLOSED_LOOP);

		protected final SubsystemControlState state;
		protected final PIDProfile.Profile profile;
		protected final TalonWrapper.PidIdx pidIdx;

		Mode(SubsystemControlState state, PIDProfile.Profile profile, TalonWrapper.PidIdx pidIdx) {
			this.state = state;
			this.profile = profile;
			this.pidIdx = pidIdx;
		}

		public static Mode from(ControlMode controlMode) {
			for(Mode m : values()){
				if(m.state.mode == controlMode) return m;
			}
			System.err.println("Using default mode, Mode.from failed");
			return HOLDING;
		}
	}

	public enum ZeroingMode {
		FORWARD(true,false),
		REVERSE(false, true),
		BOTH(true, true),
		NEITHER(false, false);

		final boolean forward, reverse;

		ZeroingMode(boolean forward, boolean reverse){
			this.forward = forward;
			this.reverse = reverse;
		}
	}

	// #endregion
}
