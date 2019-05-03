package com.team2813.frc2019;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Controls using enums to represent Axis, Button, and POV on joystick
 *
 * @author Grady Whelan
 */
public class ControlBoard {

	private static final int GAMEPAD_PORT = 0;
	private static final int JOYSTICK_PORT = 1;

	private static final Joystick GAMEPAD = new Joystick(GAMEPAD_PORT);
	private static final Joystick JOYSTICK = new Joystick(JOYSTICK_PORT);

	/**
	 * Contains all of the axes in use, assigned to a joystick
	 */
	public enum Axis {
		THROTTLE(JOYSTICK, true,1),
		TURN(JOYSTICK, true, 0, 2);

		Joystick joystick;
		int[] axes;
		boolean negate;

		/**
		 * @param joystick to use
		 * @param negate negates the output
		 * @param axesToAdd adds values from these axes together
		 */
		Axis(Joystick joystick, boolean negate, int...axesToAdd) {
			this.joystick = joystick;
			this.negate = negate;
			this.axes = axesToAdd;
		}

		/**
		 * @return output value from axes added
		 */
		public double get() {
			double output = 0.0;
			for (int axis : axes) {
				output += joystick.getRawAxis(axis);
			}
			if (negate) output = -output;
			return output;
		}
	}

	/**
	 * For any use of a joystick's POV; might implement more with pressed/released later
	 */
	public enum POV {
		NEUTRAL(JOYSTICK, -1),
		FORWARD(JOYSTICK, 0),
		RIGHT(JOYSTICK, 270),
		REVERSE(JOYSTICK, 180),
		LEFT(JOYSTICK, 90),
		CLIMB_WHEEL(JOYSTICK, 0);

		Joystick joystick;
		int angle;

		/**
		 *
		 * @param joystick to use
		 * @param angle on POV
		 */
		POV(Joystick joystick, int angle) {
			this.joystick = joystick;
			this.angle = angle;
		}

		/**
		 * @return whether the joystick POV current angle matches given angle
		 */
		public boolean inAngle() {
			return joystick.getPOV() == angle;
		}

		/**
		 * Runs a function while in given POV angle
		 *
		 * @param function to run if joystick POV current angle matches given angle
		 */
		public void whileInAngle(Runnable function) {
			if(inAngle()) function.run();
		}
	}

	/**
	 * Contains all of the buttons, with a joystick and button ID assigned to each
	 */
	public enum Button {
		SHIFT_GEARS(JOYSTICK, 1),
		CLAMP(JOYSTICK, 2),
		FRONT_PISTONS(JOYSTICK, 4),
		ELEVATOR_CLOCKWISE(GAMEPAD, 5),
		ELEVATOR_COUNTERCLOCKWISE(GAMEPAD,7),
		LEVEL_2_CLIMB(JOYSTICK, 12),
		LEVEL_3_CLIMB(JOYSTICK,10),
		RETRACT_EXDENDY(JOYSTICK, 8),
		ABORT(JOYSTICK, 3),
		HATCH_OPEN_LOOP_UP(GAMEPAD, 2),
		SNAP_DRIVE_ANGLE(JOYSTICK, 4, false), // FIXME button # - also mapped to the front pistons
		START_AUTO_LOW(JOYSTICK, 11),
		START_AUTO_MED(JOYSTICK, 9),
		START_AUTO_HIGH(JOYSTICK, 7),
		START_AUTO_LOAD(JOYSTICK, 12, false), // FIXME button # - also mapped to climb
		HATCH_LOAD_WITH_CARGO(GAMEPAD, 6),
		SLOWLY_FLOOR_ELEVATOR(GAMEPAD, 10);

		private Joystick joystick;
		private int buttonID;
		private boolean enable;

		/**
		 *
		 * @param joystick to use
		 * @param buttonID on joystick
		 * @param enable if false this button does nothing
		 */
		Button(Joystick joystick, int buttonID, boolean enable) {
			this.joystick = joystick;
			this.buttonID = buttonID;
			this.enable = enable;
		}

		/**
		 * Button always enabled
		 * @param joystick to use
		 * @param buttonID on joystick
		 */
		Button(Joystick joystick, int buttonID) {
			this(joystick, buttonID, true);
		}


		public void whenPressed(Runnable function) {
			if(getPressed()) function.run();
		}

		public void whileHeld(Runnable function) {
			if (get()) function.run();
		}

		public void whenReleased(Runnable function) {
			if (getReleased()) function.run();
		}

		/**
		 * @return current boolean value of the button
		 */
		public boolean get() {
			return enable && joystick.getRawButton(buttonID);
		}

		public boolean getPressed() {
			return enable && joystick.getRawButtonPressed(buttonID);
		}

		public boolean getReleased() {
			return enable && joystick.getRawButtonReleased(buttonID);
		}

	}

}

