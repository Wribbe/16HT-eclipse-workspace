package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class TemperatureController extends PeriodicThread {
	
	private AbstractWashingMachine machine;
	private double speed; 
	
	private double currentTemp = 0;
	private double wantedTemp = 0;
	private boolean heaterOn = false;
	private boolean continueHeating = false;
	
	private double temperatureDelta = 0.2;
	
	private int mode = TemperatureEvent.TEMP_IDLE;
	private TemperatureEvent lastEvent = null;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (10000/speed)); // 1/10sec.
		machine = mach;
		this.speed = speed;
	}
	
	private void continueHeating() {
		machine.setHeating(false);
		heaterOn = false;
		continueHeating = true;
	}
	
	private void stopAllHeating() {
		machine.setHeating(false);
		mode = TemperatureEvent.TEMP_IDLE;
		continueHeating = false;
		heaterOn = false;
		replyToEvent(lastEvent, currentTemp);
	}

	private void replyToEvent(TemperatureEvent event, double temp) {
		int mode = event.getMode();
		TemperatureEvent replyEvent = new TemperatureEvent(this, mode, temp);
		((RTThread)event.getSource()).putEvent(replyEvent);
	}
	
	private boolean temperatureInRange() {
		if (currentTemp <= (wantedTemp-1) + temperatureDelta) { // <= 58.2 @ 60.
			return false;
		} else if (currentTemp >= wantedTemp) { // >= 59 @ 60.
			return false;
		}
		return true;
	}
	
	public void perform() {

		currentTemp = machine.getTemperature();

		TemperatureEvent event = (TemperatureEvent) this.mailbox.tryFetch();

		if (event != null) {
			lastEvent = event;
			mode = event.getMode();
			if (mode == TemperatureEvent.TEMP_IDLE) {
				stopAllHeating();
			}
			wantedTemp = event.getTemperature()-1; // 59 @ 60.
		}
		
		if (continueHeating) {
			if (!temperatureInRange() && !heaterOn) {
				machine.setHeating(true);
				heaterOn = true;
			} else if(temperatureInRange() && heaterOn) {
				machine.setHeating(false);
				heaterOn = false;
			}
		}

		if(mode == TemperatureEvent.TEMP_SET) {
			if (currentTemp <= wantedTemp) { // Temperature does not match.
				if (!heaterOn) {
					machine.setHeating(true);
					heaterOn = true;
				}
			} else { // Temperature does match.
				if (!continueHeating) {
					replyToEvent(lastEvent, currentTemp);
				}
				continueHeating();
			}
		}
	}
}
