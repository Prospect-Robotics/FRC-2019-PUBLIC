package com.team2813.frc2019.subsystems;

import static com.team2813.frc2019.subsystems.Subsystems.ELEVATOR;
import static com.team2813.frc2019.subsystems.Subsystems.LOOPER;
import static com.team2813.lib.solenoid.PistonSolenoid.PistonState.EXTENDED;
import static com.team2813.lib.solenoid.PistonSolenoid.PistonState.RETRACTED;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team2813.frc2019.actions.Action;
import com.team2813.frc2019.actions.FunctionAction;
import com.team2813.frc2019.actions.SeriesAction;
import com.team2813.frc2019.actions.WaitAction;
import com.team2813.lib.solenoid.PistonSolenoid;
import com.team2813.lib.solenoid.PistonSolenoid.PistonState;
import com.team2813.lib.talon.CTREException;

public class Hatch extends Subsystem1d<Hatch.Position> {

	private static Position currentPosition = Position.HOME;

	private static final int CLAMP_SOLENOID = 5;
	private static final int OUTER_SOLENOID = 4;
	private static final int CENTER_SOLENOID = 3;

	private static PistonSolenoid clamp = new PistonSolenoid(CLAMP_SOLENOID);
	private static PistonSolenoid frontPistons = new PistonSolenoid(OUTER_SOLENOID, CENTER_SOLENOID);

	Hatch() {
		super(SubsystemMotorConfig.hatch, ZeroingMode.FORWARD);
		frontPistons.set(RETRACTED);
		clamp.set(RETRACTED);
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

	@Override
	public void outputTelemetry_() {
		// SmartDashboard.putBoolean("Clamp", clamp.get().value);
		// SmartDashboard.putBoolean("Outer/Center", outerPistons.get().value);
		// SmartDashboard.putBoolean("Center", frontPistons.get().value);
		// SmartDashboard.putString("Current Hatch Position", currentPosition.name());
		// try {
			// SmartDashboard.putNumber("Hatch Desired Encoder Position", master.getActiveTrajectoryPosition());
		// } catch (CTREException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		// }
	}

	public void loadWithCargo() {
		setNextPositionAndMove(Position.LOAD);
		ELEVATOR.setNextPositionAndMove(Elevator.Position.LOAD);
	}

	@Override
	public void setNextPosition(boolean clockwise) {
		currentPosition = currentPosition.getClock(clockwise);
		setPositionAndMove(currentPosition);
	}

	void setNextPositionAndMove(Position position) {
		currentPosition = position;
		setPositionAndMove(position);
	}

	void retractPistons() {
		frontPistons.set(RETRACTED);
		clamp.set(RETRACTED);
	}

	public void openLoopUpStart() {
		try {
			master.set(ControlMode.PercentOutput, 0.15);
		} catch (CTREException e) {
			e.printStackTrace();
		}
	}

	public void openLoopUpStop() {
		setNextPositionAndMove(Position.HOME);
	}

	public void setClamp(PistonState state) {
		clamp.set(state);
	}

	public boolean getFrontPistons() {
		return frontPistons.get().value;
	}

	public boolean getClamp() {
		return clamp.get().value;
	}

	public void toggleClamp() {
		clamp.set(clamp.get() == RETRACTED ? EXTENDED : RETRACTED);
	}

	public void autoPistons() {
		if (frontPistons.get() == PistonState.EXTENDED) {
			frontPistons.retract();
			return;
		}
		if (clamp.get() == EXTENDED) {
			Action action = new SeriesAction(
				new FunctionAction(clamp::retract, false),
				new WaitAction(0.3),
				new FunctionAction(frontPistons::extend, false)
			);
			LOOPER.addAction(action);
		} else {
			frontPistons.extend();
		}
	}
	
	public enum Position implements Subsystem1d.Position<Hatch.Position> {
		MAX(-1902) {

		},
		CLIMB(-2350) {// FIXME: 03/29/2019 THIS VALUE WORKS FOR THE COMP BOT
//		CLIMB(-3450) {// FIXME: 03/29/2019 THIS VALUE WORKS FOR THE PRACTICE BOT
			@Override
			public Position getNextClockwise() {
				return HOME; // FIXME TEMP SHOULD NOT EXIST (use in climber)
			}
			
			@Override
			public Position getNextCounter() {
				return LOAD; // FIXME TEMP SHOULD NOT EXIST (use in climber)
			}
		},
		LOAD(-600) {// TODO: 03/29/2019 tune (for comp bot first)
			@Override
			public Position getNextClockwise() {
				return CLIMB;//FIXME TEMP SHOULD BE HOME
			}

			@Override
			public Position getNextCounter() {
				return HOME;
			}
		},
		HOME(200) {
			@Override
			public Position getNextClockwise() {
				return LOAD;
			}

			@Override
			public Position getNextCounter() {
				return CLIMB;//FIXME TEMP SHOULD BE LOAD
			}
		},
		MIN(0) {

		};

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
			return MIN;
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
