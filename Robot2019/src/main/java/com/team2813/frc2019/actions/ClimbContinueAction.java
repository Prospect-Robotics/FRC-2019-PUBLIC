package com.team2813.frc2019.actions;

import com.team2813.frc2019.subsystems.Climber;

public class ClimbContinueAction implements Action {

	private Climber.Movement movement;

	public ClimbContinueAction(Climber.Movement movement) {
		this.movement = movement;
	}

	@Override
	public boolean update(double timestamp) {
		if(movement.climbContinueButton == null) return true;
		return movement.climbContinueButton.getPressed();
	}

	@Override
	public void start(double timestamp) {

	}

	@Override
	public void end(double timestamp) {

	}
}
