package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class TemperatureController extends PeriodicThread {
	// TODO: add suitable attributes

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
		System.out.println("TEMPRATURE CONTROLLER CONSTRUCTOR!");
	}

	public void perform() {
		System.out.println("TEMPRATURE CONTROLLER PREFORM!");
		// TODO: implement this method
	}
}
