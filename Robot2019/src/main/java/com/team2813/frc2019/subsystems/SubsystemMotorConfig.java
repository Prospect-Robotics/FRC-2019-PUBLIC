package com.team2813.frc2019.subsystems;

import static com.team2813.lib.talon.options.PIDProfile.Profile.PRIMARY;
import static com.team2813.lib.talon.options.PIDProfile.Profile.SECONDARY;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2813.lib.talon.BaseMotorControllerWrapper.PidIdx;
import com.team2813.lib.talon.BaseMotorControllerWrapper.TimeoutMode;
import com.team2813.lib.talon.CTREException;
import com.team2813.lib.talon.TalonWrapper;
import com.team2813.lib.talon.VictorWrapper;
import com.team2813.lib.talon.options.HardLimitSwitch;
import com.team2813.lib.talon.options.Inverted;
import com.team2813.lib.talon.options.LimitDirection;
import com.team2813.lib.talon.options.PIDProfile;
import com.team2813.lib.talon.options.Slave;
import com.team2813.lib.talon.options.SoftLimit;
import com.team2813.lib.talon.options.TalonOptions;
import com.team2813.lib.talon.options.VictorOptions;

/**
 * @author Adrian Guerra
 */
class SubsystemMotorConfig {

	//#region Drive
	/**
	 * Configuration for Talon Drive Left ID 2
	 */
	@PIDProfile(
		profile = PRIMARY,
		p = 0.9,//254 had 0.9 in low gear
		i = 0,
		f = 0,
		d = 10.0,//254 had 10.0 in low gear
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@PIDProfile(
		profile = SECONDARY,
		p = 0,
		i = 0,
		d = 0,
		f = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@TalonOptions(
		deviceNumber = 2,
		subsystemName = "Drive",
		peakCurrentDuration = 0,
		peakCurrentLimit = 0,
		feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
		compSaturationVoltage = 12,
		continuousCurrentLimitAmps = 30,
		motionAcceleration = 0,
		motionCruiseVelocity = 0,
		closedLoopRampRate = 0,
		openLoopRampRate = 0,
		enableVoltageCompensation = true,
		invertSensorPhase = true,
		statusFrame = StatusFrameEnhanced.Status_2_Feedback0)
	@Slave(
		id = 1)
	@Inverted
	static TalonWrapper driveLeft;

	/**
	 * Configuration for Talon Drive Right ID 12
	 */
	@PIDProfile(
		profile = PRIMARY,
		p = 0.9,
		i = 0,
		f = 0,
		d = 10.0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@PIDProfile(
		profile = SECONDARY,
		p = 0,
		i = 0,
		d = 0,
		f = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@TalonOptions(
		deviceNumber = 12,
		subsystemName = "Drive",
		peakCurrentDuration = 0,
		peakCurrentLimit = 0,
		feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
		compSaturationVoltage = 12,
		continuousCurrentLimitAmps = 30,
		motionAcceleration = 0,
		motionCruiseVelocity = 0,
		closedLoopRampRate = 0,
		openLoopRampRate = 0,
		enableVoltageCompensation = true,
		invertSensorPhase = true,
		statusFrame = StatusFrameEnhanced.Status_2_Feedback0)
	@Slave(
		id = 11)
	static TalonWrapper driveRight;

	// #endregion

	//#region Elevator

	/**
	 * Configuration for Talon Elevator ID 4
	 */
	@PIDProfile(
		profile = PRIMARY,
		p = 0.6,
		i = 0,
		f = 0,
		d = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@PIDProfile(
		profile = SECONDARY,
		p = 0.8,
		i = 0,
		d = 0,
		f = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@HardLimitSwitch(
		direction = LimitDirection.REVERSE,
		clearOnLimit = true,
		enabled = true)
	@SoftLimit(
		direction = LimitDirection.FORWARD,
		clearOnLimit = false,
		threshold = 103745,
		enabled = true)
	@TalonOptions(
		deviceNumber = 4,
		subsystemName = "Elevator",
		peakCurrentDuration = 0,
		peakCurrentLimit = 0,
		feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
		compSaturationVoltage = 12,
		continuousCurrentLimitAmps = 40,
		motionAcceleration = 28000,
		motionCruiseVelocity = 40000,
		closedLoopRampRate = 0.001,
		openLoopRampRate = 0.001,
		enableVoltageCompensation = true,
		invertSensorPhase = true,
		statusFrame = StatusFrameEnhanced.Status_2_Feedback0)
	@Slave(
		id = 9, // Right
		followMode = InvertType.OpposeMaster)
	@Slave(
		id = 7, // Right
		followMode = InvertType.OpposeMaster)
	@Slave(
		id = 3 // Left
	)
	@Inverted
	static TalonWrapper elevator;

	//#endregion

	//#region Climber

	@PIDProfile(
		profile = PRIMARY,
		p = 0.7,
		i = 0,
		f = 0,
		d = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@PIDProfile(
		profile = SECONDARY,
		p = 0,
		i = 0,
		d = 0,
		f = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
//	@HardLimitSwitch(// FIXME: 03/26/2019 add if there's a limit switch
//		direction = LimitDirection.FORWARD,
//		enabled = true,
//		clearOnLimit = true
//	)
	@TalonOptions(
		deviceNumber = 10,
		subsystemName = "Climber",
		peakCurrentDuration = 0,
		peakCurrentLimit = 0,
		feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
		compSaturationVoltage = 12,
		continuousCurrentLimitAmps = 60,
		motionAcceleration = 8000,
		motionCruiseVelocity = 5000,
		closedLoopRampRate = 0.001,
		openLoopRampRate = 0.001,
		enableVoltageCompensation = true,
		invertSensorPhase = false,
		statusFrame = StatusFrameEnhanced.Status_2_Feedback0)
	@Inverted
	static TalonWrapper climberExdendy;

	@VictorOptions(
			deviceNumber = 6,
			subsystemName = "Climber",
			feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
			reverseLimitSwitchEnable = false,
			reverseSoftLimit = 0,
			reverseSoftLimitEnable = false,
			compSaturationVoltage = 0,
			motionAcceleration = 0,
			motionCruiseVelocity = 0,
			closedLoopRampRate = 0,
			openLoopRampRate = 0,
			enableVoltageCompensation = true,
			invertSensorPhase = true,
			statusFrame = StatusFrame.Status_2_Feedback0)
	@Inverted
	static VictorWrapper climberWheel;

	// #endregion

	//#region Hatch
	/**
	 * Configuration for Talon Hatch ID 8
	 */
	@PIDProfile(
		profile = PRIMARY,
		p = 0.9,
		i = 0,
		f = 0,
		d = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@PIDProfile(
		profile = SECONDARY,
		p = 0.6,
		i = 0,
		d = 0,
		f = 0,
		maxIntegralAccumulator = 0,
		integralZone = 0,
		allowableClosedLoopError = 0)
	@HardLimitSwitch(
		enabled = true,
		direction = LimitDirection.FORWARD,
		clearOnLimit = true)
	@SoftLimit(// For now, do not enable this soft limit. It will not affect anything as we are always using closed loop movement within appropriate bounds
		enabled = false,
		direction = LimitDirection.FORWARD,
		threshold = 2550,
		clearOnLimit = false)
	@TalonOptions(
		deviceNumber = 8,
		subsystemName = "Hatch",
		peakCurrentDuration = 0,
		peakCurrentLimit = 0,
		feedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative,
		compSaturationVoltage = 12,
		continuousCurrentLimitAmps = 20,
		motionAcceleration = 1000,
		motionCruiseVelocity = 2500,
		closedLoopRampRate = 0.001,
		openLoopRampRate = 0.001,
		enableVoltageCompensation = true,
		invertSensorPhase = true,
		statusFrame = StatusFrameEnhanced.Status_2_Feedback0)
	static TalonWrapper hatch;

	// #endregion

	//#region Initialization
	static {
		initializeTalons(); // TODO call from somewhere else
	}

	private static void initializeTalons() {
		List<Integer> talonIds = new ArrayList<>();
		for (Field field : SubsystemMotorConfig.class.getDeclaredFields()) {
			if (field.getType() == TalonWrapper.class) {
				try {
					TalonOptions options = field.getAnnotation(TalonOptions.class);
					System.out.println("Configuring " + options.subsystemName());
					// TODO exception if no options present

					for (Integer id : talonIds)
						if (id == options.deviceNumber()) {
							System.err.println("Tried to register talon with already used id");
						}

					talonIds.add(options.deviceNumber());

					TalonWrapper talon = new TalonWrapper(options.deviceNumber(), options.subsystemName());

					talon.setFactoryDefaults();

					talon.setPeakCurrentDuration(options.peakCurrentDuration());
					talon.setPeakCurrentLimit(options.peakCurrentLimit());

					talon.setSelectedFeedbackSensor(options.feedbackSensor(), PidIdx.PRIMARY_CLOSED_LOOP);

					talon.setVoltageCompensationSaturation(options.compSaturationVoltage());
					talon.setVoltageCompensationEnabled(options.enableVoltageCompensation());

					talon.setOpenLoopRamp(options.openLoopRampRate());
					talon.setClosedLoopRamp(options.closedLoopRampRate());

					talon.setSensorPhaseInverted(options.invertSensorPhase());

					talon.setStatusFramePeriod(options.statusFrame(), options.statusFramePeriod());

					talon.setMotionMagicCruiseVelocity(options.motionCruiseVelocity());
					talon.setMotionMagicAcceleration(options.motionAcceleration());

					talon.setContinuousCurrentLimit(options.continuousCurrentLimitAmps());

					talon.setVelocityMeasurementPeriod(options.velocityMeasurementPeriod());
					talon.setVelocityMeasurementWindow(options.velocityMeasurementWindow());

					for (HardLimitSwitch hardLimitSwitch : field.getAnnotationsByType(HardLimitSwitch.class)) {
						System.out.println("\tconfiguring hard limit switch " + hardLimitSwitch.direction());
						talon.setLimitSwitchSource(hardLimitSwitch.direction(), hardLimitSwitch.limitSwitchSource(),
							hardLimitSwitch.limitSwitchNormal());
						talon.setDirectionParameterForLimit(hardLimitSwitch.direction(), hardLimitSwitch.paramValue(),
							hardLimitSwitch.paramSubValue(), hardLimitSwitch.paramOrdinal(),
							hardLimitSwitch.clearOnLimit());
					}

					for (SoftLimit softLimit : field.getAnnotationsByType(SoftLimit.class)) {
						System.out.println("\tconfiguring soft limit " + softLimit.direction());
						talon.setSoftLimit(softLimit.direction(), softLimit.threshold(), softLimit.enabled());
						talon.setDirectionParameterForLimit(softLimit.direction(), softLimit.paramValue(),
							softLimit.paramSubValue(), softLimit.paramOrdinal(), softLimit.clearOnLimit());
						talon.setClearPositionOnLimit(softLimit.direction(), softLimit.clearOnLimit());
					}

					for (PIDProfile profile : field.getAnnotationsByType(PIDProfile.class)) {
						System.out.println("\tconfiguring pid profile " + profile.profile());
						talon.setPIDF(profile.profile(), profile.p(), profile.i(), profile.d(), profile.f());
					}

					Inverted inverted = field.getAnnotation(Inverted.class);
					if (inverted != null)
						talon.setInverted(inverted.type());
					else
						talon.setInverted(InvertType.None);

					for (Slave slave : field.getAnnotationsByType(Slave.class)) {
						System.out.println(
							"\tCreating slave w/ id of " + slave.id() + " of type " + slave.motorControllerType()
								+ " with follow mode " + slave.followMode() + " on " + options.subsystemName());
						switch (slave.motorControllerType()) {
						case VICTOR:
							VictorSPX victorSlave = new VictorSPX(slave.id());
							victorSlave.follow(talon.motorController);
							victorSlave.setInverted(slave.followMode());
							break;
						case TALON:
							TalonWrapper talonSlave = new TalonWrapper(slave.id());
							talonSlave.set(ControlMode.Follower, talon.getDeviceID());
							talonSlave.setInverted(slave.followMode());
							break;
						}
					}

					talon.timeoutMode = TimeoutMode.RUNNING;

					field.set(null, talon);

				} catch (CTREException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			} else if (field.getType() == VictorWrapper.class) {
				try {
					VictorOptions options = field.getAnnotation(VictorOptions.class);
					System.out.println("Configuring " + options.subsystemName());
					// TODO exception if no options present

					VictorWrapper victor = new VictorWrapper(options.deviceNumber(), options.subsystemName());

					victor.setSelectedFeedbackSensor(options.feedbackSensor(), PidIdx.PRIMARY_CLOSED_LOOP);

					victor.setLimitSwitches(options.reverseLimitSwitchEnable());

					victor.setReverseSoftLimit(options.reverseSoftLimit(), options.reverseSoftLimitEnable());

					victor.setVoltageCompensationSaturation(options.compSaturationVoltage());
					victor.setVoltageCompensationEnabled(options.enableVoltageCompensation());

					victor.setOpenLoopRamp(options.openLoopRampRate());
					victor.setClosedLoopRamp(options.closedLoopRampRate());

					victor.setSensorPhaseInverted(options.invertSensorPhase());

					victor.setStatusFramePeriod(options.statusFrame(), options.statusFramePeriod());

					victor.setMotionMagicCruiseVelocity(options.motionCruiseVelocity());
					victor.setMotionMagicAcceleration(options.motionAcceleration());

					victor.setForwardLimitSwitchSource(options.forwardLimitSwitchSource(),
						options.forwardLimitSwitchNormal());

					victor.setVelocityMeasurementPeriod(options.velocityMeasurementPeriod());
					victor.setVelocityMeasurementWindow(options.velocityMeasurementWindow());

					victor.setClearPositionOnLimitF(options.clearOnLimitFwd());

					for (PIDProfile profile : field.getAnnotationsByType(PIDProfile.class)) {
						System.out.println("\tconfiguring pid profile " + profile.profile());
						victor.setPIDF(profile.profile(), profile.p(), profile.i(), profile.d(), profile.f());
					}

					Inverted inverted = field.getAnnotation(Inverted.class);
					if (inverted != null)
						victor.setInverted(inverted.type());
					else
						victor.setInverted(InvertType.None);

					for (Slave slave : field.getAnnotationsByType(Slave.class)) {
						System.out.println(
							"\tCreating slave w/ id of " + slave.id() + " of type " + slave.motorControllerType()
								+ " with follow mode " + slave.followMode() + " on " + options.subsystemName());
						switch (slave.motorControllerType()) {
						case VICTOR:
							VictorSPX victorSlave = new VictorSPX(slave.id());
							victorSlave.follow(victor.motorController);
							victorSlave.setInverted(slave.followMode());
							break;
						case TALON:
							TalonWrapper talonSlave = new TalonWrapper(slave.id());
							talonSlave.set(ControlMode.Follower, victor.getDeviceID());
							talonSlave.setInverted(slave.followMode());
							break;
						}
					}

					victor.timeoutMode = TimeoutMode.RUNNING;

					field.set(null, victor);

				} catch (CTREException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	// #endregion
}
