package todo;

import done.*;

public class WashingController implements ButtonListener {	
	
	private WashingProgram currentProgram = null;
	
	private final int STOP = 0;
	
    public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
		// TODO: implement this constructor
    }
    
    private boolean programIsRunning() {
    	return currentProgram != null;
    }
    
    private boolean emergencyStopPressed(int button) {
    	return button == STOP;
    }

    public void processButton(int button) {

		if (programIsRunning()) {
			if (emergencyStopPressed(button)) {
				System.out.println("Stop button pressed during program.");
				currentProgram = null;
			} else {
				// Ignore press.
			}
		}
		startProgram(button);
    }
    
    private void startProgram(int button) {
    	WashingProgram program = null; 
    	switch(button) {
			case 1: program = new WashingProgram1();
			break;
			case 2: program = new WashingProgram2();
			break;
			case 3: program = new WashingProgram3();
			break;
			default: // Ingore;
    	}
    	currentProgram = program;
    }
}
