package com.team2813.frc2019.actions;

import static com.team2813.frc2019.subsystems.Subsystems.DRIVE;

import com.team2813.frc2019.Robot;

import edu.wpi.first.wpilibj.Timer;

public class TimedDriveAction implements Action {

    private double throttle;
    private double turn;
    private double time;
    private double initialTime;

    public TimedDriveAction(double throttle, double turn, double time) {
        this.throttle = throttle;
        this.turn = turn;
        this.time = time;
    }

    @Override
    public boolean update(double timestamp) {
        DRIVE.setOpenLoop(
            Robot.driveSignalGenerator.arcadeDrive(throttle,
                turn, true));
        return Timer.getFPGATimestamp() - initialTime > time;
    }

    @Override
    public void start(double timestamp) {
        initialTime = Timer.getFPGATimestamp();
    }

	@Override
	public void end(double timestamp) {

	}

	@Override
	public boolean getRemoveOnDisabled() {
		return true;
	}

}