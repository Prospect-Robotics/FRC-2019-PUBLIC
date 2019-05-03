package com.team2813.frc2019.actions;

import static com.team2813.frc2019.subsystems.Subsystems.ELEVATOR;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2813.frc2019.Robot;
import com.team2813.frc2019.subsystems.Elevator;
import com.team2813.lib.talon.CTREException;

/**
 * @author Grady Whelan
 */
public class FloorElevatorAction implements Action {

	@Override
	public boolean update(double timestamp) {
		try {
			ELEVATOR.master.set(ControlMode.PercentOutput, -0.3);
			return ELEVATOR.master.isReverseLimitSwitchClosed();
		} catch (CTREException e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public void start(double timestamp) {
		//FIXME REMOVE THIS:
		try {
			ELEVATOR.master.setVoltageCompensationSaturation(40);
		} catch (CTREException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void end(double timestamp) {
		ELEVATOR.setNextPosition(Elevator.Position.LOW);
		Robot.autoElevator = false;
	}

	@Override
	public boolean getRemoveOnDisabled() {
		return false; // This is false because we don't want the elevator to change its plans after the sandstorm
	}

}