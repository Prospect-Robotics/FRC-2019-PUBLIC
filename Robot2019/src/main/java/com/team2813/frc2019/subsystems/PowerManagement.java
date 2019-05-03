package com.team2813.frc2019.subsystems;

import com.team2813.lib.talon.CTREException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SensorUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team2813.lib.logging.LogLevel.*;

class PowerManagement extends Subsystem {

	public final PowerDistributionPanel pdp = new PowerDistributionPanel();

	private static final double VOLTAGE_WARNING_CUTOFF = 12.1;
	private static final double VOLTAGE_ERROR_CUTOFF = 11.6;
	//TODO critical error

	@Override
	protected boolean checkSystem_() throws CTREException {
		return false;
	}

	public void putToDashboard(){
		SmartDashboard.putData("PDP", pdp);
	}

	@Override
	protected void outputTelemetry_() throws CTREException {
		
		// PDP
//		SmartDashboard.putNumber("PDP voltage", pdp.getVoltage());
//		SmartDashboard.putNumber("PDP total power watts", pdp.getTotalPower());
//		SmartDashboard.putNumber("PDP total current amps", pdp.getTotalCurrent());
//		SmartDashboard.putNumber("PDP total energy joules", pdp.getTotalEnergy());
//		for(int channel = 0; channel < SensorUtil.kPDPChannels; channel++){
//			SmartDashboard.putNumber("PDP channel "+channel+" current amps", pdp.getCurrent(channel));
//		}

	}

	@Override
	protected void onAnyLoop_(double timestamp) {
		double voltage = pdp.getVoltage();
		if(voltage <= VOLTAGE_ERROR_CUTOFF){
			ERROR.log("Battery voltage is",voltage,"should never be below",VOLTAGE_ERROR_CUTOFF);
		}
		else if(voltage <= VOLTAGE_WARNING_CUTOFF){
			WARNING.log("Battery voltage is",voltage,"should never be below",VOLTAGE_WARNING_CUTOFF);
		}
	}

	@Override
	protected void onEnabledStart_(double timestamp) {}

	@Override
	protected void onEnabledLoop_(double timestamp) {}

	@Override
	protected void onEnabledStop_(double timestamp) {}

}