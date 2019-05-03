package com.team2813.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.team2813.frc2019.Robot;
import com.team2813.frc2019.actions.*;
import com.team2813.frc2019.ControlBoard;
import com.team2813.lib.talon.CTREException;

import static com.team2813.frc2019.subsystems.Subsystems.*;
import static com.team2813.lib.logging.LogLevel.DEBUG;

public class Climber extends Subsystem1d<Climber.Position> {

	private static Position currentPosition = Position.HOME;
	private Action startAction;
	private Action abortAction;
	private Action retractAction;

	private final int ELEVATOR_VELOCITY = 11636;// approx exdendy velocity (8000) * 1.7545454545
	private final int ELEVATOR_ACCELERATION = 5000;


	Climber() {
		super(SubsystemMotorConfig.climberExdendy, ZeroingMode.NEITHER);
	}


	@Override // TODO check Talons
	public boolean checkSystem_() {

		return true;
	}
	
	public void outputTelemetry_() {
//		String s;
//		try {
//			s = "" + SubsystemMotorConfig.climberExdendy.getSelectedSensorPosition();
//			SmartDashboard.putString("Climber Encoder Position", s);
//		} catch (CTREException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SmartDashboard.putString("Climber Height Identifier", currentPosition.toString());
//		SmartDashboard.putString("Climber Control State", currentMode.state.toString());
//		SmartDashboard.putString("Climber Demand", "" + mPeriodicIO.demand);
//		SmartDashboard.putString("Climber Actual Control Mode", ""+ SubsystemMotorConfig.climberExdendy.getControlMode());
//		try {
//			SmartDashboard.putString("Climber Actual Desired Position",
//					"" + SubsystemMotorConfig.climberExdendy.getActiveTrajectoryPosition());
//			SmartDashboard.putNumber("Motion Cruise Velocity", SubsystemMotorConfig.climberExdendy.motorController.getActiveTrajectoryVelocity());
//		} catch (CTREException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	public void setNextPosition(Position position) {
		currentPosition = position;
		setPosition(currentPosition);
	}
	
	public void setWheelMoving() {
		try {
			SubsystemMotorConfig.climberWheel.set(ControlMode.PercentOutput, 1.0);
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setWheelStopped() {
		try {
			SubsystemMotorConfig.climberWheel.set(ControlMode.PercentOutput, 0.0);
		} catch (CTREException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void climb(Movement movement) {
		Robot.climbInitiated = true;
		if (movement == Movement.RETRACT) return;
		DEBUG.log("Starting Climb");
		startAction = new SeriesAction(
			new FunctionAction(HATCH::retractPistons, true),
			new FunctionAction(() -> ELEVATOR.setPositionAndMove(movement.elevatorStartPosition), true),
			new WaitAction(0.5),
			new FunctionAction(() -> HATCH.setNextPositionAndMove(Hatch.Position.CLIMB), true),
//			new WaitAction(6),//Enough time for Hatch arm to fully extend and elevator up // FIXME: 03/26/2019 add back in (testing)
//			new FunctionAction(() -> ELEVATOR.resetPIDF(0,0,0,0), true),
			new FunctionAction(() -> ELEVATOR.setMode(Mode.HOLDING), true),// this changes the PID values
			new ClimbContinueAction(movement),
			new FunctionAction(
				() -> {
					try {
						ELEVATOR.master.setMotionMagicCruiseVelocity(ELEVATOR_VELOCITY);
					} catch (CTREException e) {
						e.printStackTrace();
					}
				}, true
			),
			new FunctionAction(
				() -> {
					try {
						ELEVATOR.master.setMotionMagicAcceleration(ELEVATOR_ACCELERATION);
					} catch (CTREException e) {
						e.printStackTrace();
					}
				}, true
			),
			// this ensures we keep set PID values
			new FunctionAction(() -> ELEVATOR.setPositionWithMode(movement.elevatorClimbPosition, Mode.HOLDING), true),
			new FunctionAction(() -> setPositionAndMove(movement.exdendyPosition), true)
		);
		LOOPER.addAction(startAction);
		Robot.isClimbing = true;
	}

	public void retractExdendy() {
		DEBUG.log("Exdendy Retracting");
//		HATCH.setPositionAndMove(Hatch.Position.HOME); // FIXME this is for the quick fix for end of SVR
		setPositionAndMove(Position.HOME);
	}

	public void abort() {// should this be an action? Without a wait it doesn't really matter...
		abortAction = new SeriesAction(
			new FunctionAction(() -> ELEVATOR.setNextPositionAndMove(Elevator.Position.MED), true),
			new FunctionAction(() -> setPositionAndMove(Position.HOME), true)//,
//			new FunctionAction(() -> HATCH.setPositionAndMove(Hatch.Position.HOME), true) // FIXME this is for the quick fix for end of SVR
		);
		LOOPER.addAction(abortAction);
		Robot.climbInitiated = false;
		Robot.isClimbing = false;
	}

	public void setNextPositionAndMove(Position position) {
		currentPosition = position;
		setPositionAndMove(position);
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	//FIXME needs correct measurements
	public enum Position implements Subsystem1d.Position<Climber.Position> {
		HOME(0),
		TWO(8176),//TODO tune
		THREE(22869);

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
			return HOME;
		}

		@Override
		public Position getMax() {
			return THREE;
		}

		@Override
		public Position getNextCounter() {
			return this;
		}

        @Override
        public Position getNextClockwise() {
            return this;
        }
	}

    @Override
    public void setNextPosition(boolean clockwise) {

    }

	/**
	 * @author Grady Whelan
	 */
	public enum Movement {
		TWO(Position.TWO, Elevator.Position.CLIMB_LOW, Elevator.Position.CLIMB_TWO, ControlBoard.Button.LEVEL_2_CLIMB),
		THREE(Position.THREE, Elevator.Position.CLIMB_LOW, Elevator.Position.CLIMB_THREE, ControlBoard.Button.LEVEL_3_CLIMB),
		RETRACT(Position.HOME, Elevator.Position.CLIMB_LOW, Elevator.Position.LOW),
		ABORT(Position.HOME, Elevator.Position.MED, Elevator.Position.LOW);

		public Position exdendyPosition;
		public Elevator.Position elevatorClimbPosition;
		public Elevator.Position elevatorStartPosition;
		public ControlBoard.Button climbContinueButton;

		Movement(Position exdendyPosition, Elevator.Position elevatorClimbPosition,
				 Elevator.Position elevatorStartPosition, ControlBoard.Button climbContinueButton) {
			this.exdendyPosition = exdendyPosition;
			this.elevatorClimbPosition = elevatorClimbPosition;
			this.elevatorStartPosition = elevatorStartPosition;
			this.climbContinueButton = climbContinueButton;
		}

		Movement(Position exdendyPosition, Elevator.Position elevatorClimbPosition, Elevator.Position elevatorStartPosition) {
			this.exdendyPosition = exdendyPosition;
			this.elevatorClimbPosition = elevatorClimbPosition;
			this.elevatorStartPosition = elevatorStartPosition;
			this.climbContinueButton = null;
		}
	}
}
