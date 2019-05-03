package com.team2813.frc2019.subsystems;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.team2813.frc2019.Robot.RobotMode;
import com.team2813.frc2019.loops.Looper;
import com.team2813.frc2019.loops.Loop;

public class Subsystems {

	public static final List<Subsystem> allSubsystems;

	public static final Drive DRIVE;
	public static final Elevator ELEVATOR;
	public static final Hatch HATCH;
	public static final PowerManagement POWER_MANAGEMENT;
	public static final Climber CLIMBER;
	public static final Looper LOOPER = new Looper(RobotMode.DISABLED); //FIXME put looper somewhere else

	private static class SmartDashboardLoop implements Loop{
		int currentSubsystem = 0;
		
		@Override
		public void onAnyLoop(double timestamp){
			if(allSubsystems.size() == 0) return;
			if(currentSubsystem >= allSubsystems.size()) currentSubsystem = 0;
			allSubsystems.get(currentSubsystem).outputTelemetry();
			currentSubsystem++;
		}
	};

	static {
		DRIVE = new Drive();
		ELEVATOR = new Elevator();
		HATCH = new Hatch();
		POWER_MANAGEMENT = new PowerManagement();
		CLIMBER = new Climber();
		allSubsystems = Collections.unmodifiableList(Arrays.asList(
			DRIVE,
			ELEVATOR,
			HATCH,
			POWER_MANAGEMENT,
			CLIMBER
		));
		LOOPER.addLoop(new SmartDashboardLoop());
	}
}