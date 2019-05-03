package com.team2813.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team2813.frc2019.Robot;
import com.team2813.frc2019.actions.FloorElevatorAction;
import com.team2813.lib.talon.CTREException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team2813.frc2019.subsystems.Subsystems.LOOPER;

public class Elevator extends Subsystem1d<Elevator.Position> {

	private static Position currentPosition = Position.LOW;

	Elevator() {
		super(SubsystemMotorConfig.elevator, ZeroingMode.REVERSE);
	}


	@Override // TODO check Talons
	public boolean checkSystem_() {
		try {
			master.setNeutralMode(NeutralMode.Coast);
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void outputTelemetry_() {
		String s;
		try {
			s = "" + SubsystemMotorConfig.elevator.getSelectedSensorPosition();
			SmartDashboard.putString("Elevator Encoder Position", s);
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SmartDashboard.putString("Elevator Height Identifier", currentPosition.toString());
		SmartDashboard.putString("Elevator Control State", currentMode.state.toString());
		SmartDashboard.putString("Elevator Demand", "" + mPeriodicIO.demand);
		SmartDashboard.putString("Elevator Actual Control Mode", ""+ SubsystemMotorConfig.elevator.getControlMode());
		try {
			SmartDashboard.putString("Elevator Actual Desired Position",
					"" + SubsystemMotorConfig.elevator.getActiveTrajectoryPosition());
			SmartDashboard.putNumber("Motion Cruise Velocity", SubsystemMotorConfig.elevator.motorController.getActiveTrajectoryVelocity());
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setNextPosition(boolean clockwise) {
		currentPosition = currentPosition.getClock(clockwise);
		setPositionAndMove(currentPosition);
	}

	public void setNextPosition(Position position) {
		currentPosition = position;
		setPosition(currentPosition);
	}

	public void setNextPositionAndMove(Position position) {
		currentPosition = position;
		setPositionAndMove(currentPosition);
	}

	public void slowlyFloor() {
		if (!Robot.autoElevator) {
			LOOPER.addAction(new FloorElevatorAction());
			Robot.autoElevator = true;
		}
	}

	//FIXME needs correct measurements
	public enum Position implements Subsystem1d.Position<Elevator.Position> {
		CLIMB_LOW(-10000),
		LOW(-2000){
			@Override
			public Position getNextClockwise() {
				return MED;
			}

			@Override
			public Position getNextCounter() {
				return HIGH;
			}
		},
		LOAD(5000),
		CLIMB_TWO(10172),// tuned
		CLIMB_THREE(34880),// tuned
		MED(50000){
			@Override
			public Position getNextClockwise() {
				return HIGH;
			}

			@Override
			public Position getNextCounter() {
				return LOW;
			}
		},
		HIGH(100150){
			@Override
			public Position getNextClockwise() {
				return LOW;
			}

			@Override
			public Position getNextCounter() {
				return MED;
			}
		},
		MAX(103745);

		private final int position;

		Position(int position) {
			this.position = position;
		}
		@Override
		public int getPos() {
			return position;
		}

		@Override
		public Position getMin() {
			return LOW;
		}

		@Override
		public Position getMax() {
			return MAX;
		}

		@Override
		public Position getNextClockwise() {
			return this;
		}

		@Override
		public Position getNextCounter() {
			return this;
		}
	}
}
