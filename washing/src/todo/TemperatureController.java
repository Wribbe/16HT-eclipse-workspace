package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class TemperatureController extends PeriodicThread {
	
	private AbstractWashingMachine machine;
	private double speed; 

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
		machine = mach;
		this.speed = speed;
	}

	public void perform() {
		TemperatureEvent event = (TemperatureEvent) this.mailbox.tryFetch();
		if (event != null) {
			machine.setHeating(true);
		}
	}
}
