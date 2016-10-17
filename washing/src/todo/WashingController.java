package todo;

import done.*;

public class WashingController implements ButtonListener {	
	// TODO: add suitable attributes
	
    public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
		// TODO: implement this constructor
    }

    public void processButton(int theButton) {
    	switch (theButton) {
			case 0: System.out.println("Pressed button 0");
			break;
			case 1: System.out.println("Pressed button 1");
			break;
			case 2: System.out.println("Pressed button 2");
			break;
			case 3: System.out.println("Pressed button 3");
			break;
    	}
    }
}
