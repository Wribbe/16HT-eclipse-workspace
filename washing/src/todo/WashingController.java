package todo;

import done.*;

public class WashingController implements ButtonListener {	
	
	// ------------------------------------------------------------- Private variables.
	private WashingProgram currentProgram = null;
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
    
    private boolean programIsRunning() {
    	return currentProgram != null;
    }
    
    private boolean emergencyStopPressed(int button) {
    	return button == STOP;
    }
    
    public void processButton(int button) {
    	
    	if (programIsRunning() && emergencyStopPressed(button)) {
    		System.out.println("Abort!");
    		currentProgram.interrupt();
    	} else if (emergencyStopPressed(button) || (programIsRunning() && !emergencyStopPressed(button))) {
    		// * Ignore emergency stop if not running.
    		// * Ignore program buttons when program is running. 
    	} else { // Set prorgam and start it.
			setProgram(button);
			currentProgram.start();
    	}
    }
    
    private void setProgram(int button) {
    	WashingProgram program = null; 
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
