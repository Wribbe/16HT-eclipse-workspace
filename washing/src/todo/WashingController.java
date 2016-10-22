package todo;

import done.*;
import todo.WashingProgram.WashingDoneException;

public class WashingController implements ButtonListener {	
	
	// ------------------------------------------------------------- Private variables.
	private ExtendedWashingProgram currentProgram = null;
	private final int STOP = 0;
	private AbstractWashingMachine machine;
	private double speed;

	// ------------------------------------------------------------- Controllers.
	private TemperatureController temp;
	private SpinController spin;
	private WaterController water;
	
    public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
    	speed = theSpeed;
    	temp = new TemperatureController(theMachine, speed);
    	spin = new SpinController(theMachine, speed);
    	water = new WaterController(theMachine, speed);
    	machine = theMachine;
    	
    	// Start the controller threads.
    	temp.start();
    	spin.start();
    	water.start();
    }
    
    private boolean programBound() {
    	return currentProgram != null;
    }
    
    private boolean programRunning() {
    	return programBound() && !currentProgram.done;
    }
    
    private boolean emergencyStopPressed(int button) {
    	return button == STOP;
    }
    
    public void processButton(int button) {
    	
    	if (programRunning()) {
    		if (emergencyStopPressed(button)) {
    			ExtendedWashingProgram programToBeAborted = currentProgram;
    			currentProgram = null;
    			programToBeAborted.interrupt();
    		} else {
    			// Ignore all other button presses when running.
    		}
    	} else { // Nothing running, set a program and start.
			setProgram(button); 
			if (programBound()) {
				currentProgram.start();
			}
    	}
    }
    
    private void setProgram(int button) {
    	ExtendedWashingProgram program = null; 
    	switch(button) {
			case 1: program = new WashingProgram1(machine, speed, temp, water, spin);
			break;
			case 2: program = new WashingProgram2(machine, speed, temp, water, spin);
			break;
			case 3: program = new WashingProgram3(machine, speed, temp, water, spin);
			break;
			default: // Ingore;
    	}
    	currentProgram = program;
    }
}
