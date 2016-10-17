package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class WaterController extends PeriodicThread {
	
	private AbstractWashingMachine machine;
//	private double speed;
	
	
	private double wantedLevel = 0;
	private double currentLevel = 0;
	
	private int mode = WaterEvent.WATER_IDLE;
	private boolean pumpOn = false;
	private boolean drainOn = false;
	
	private WaterEvent lastEvent = null;
	
	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (100)); // TODO: replace with suitable period
		machine = mach;
//		this.speed = speed;
	}
	
	private boolean levelLowerThenWanted() {
		return currentLevel < wantedLevel;
	}

	private boolean levelHigherThenWanted() {
		return currentLevel > wantedLevel;
	}

	private void setIdle() {
		this.mode = WaterEvent.WATER_IDLE;
	}
	
	private void replyToEvent(WaterEvent event, double level) {
		int mode = event.getMode();
		WaterEvent replyEvent = new WaterEvent(this, mode, level);
		((RTThread)event.getSource()).putEvent(replyEvent);
	}

	public void perform() {

		WaterEvent getEvent = (WaterEvent)this.mailbox.tryFetch();
		
		if (getEvent != null) {
			wantedLevel = getEvent.getLevel();
			mode = getEvent.getMode();
			lastEvent = getEvent;
		}

		currentLevel = machine.getWaterLevel();
		
		if(mode == WaterEvent.WATER_FILL) {
			if (levelLowerThenWanted() && !pumpOn) { // Turn on pump.
				machine.setFill(true);
				pumpOn = true;
				System.out.println("Starting pump.");
			} if(pumpOn && !levelLowerThenWanted()) { // Turn of pump, level matched.
				setIdle();
				replyToEvent(lastEvent, currentLevel);
			}
		} else if (mode == WaterEvent.WATER_DRAIN) {
			if(levelHigherThenWanted() && !drainOn ) { // Turn on drain.
				machine.setDrain(true);
				drainOn = true;
				System.out.println("Starting drain.");
			} else if(drainOn && !levelHigherThenWanted()) { // Turn of drain, level matched.
				setIdle();
				replyToEvent(lastEvent, currentLevel);
			}
		} else { // Mode set to idle.
			machine.setFill(false);
			machine.setDrain(false);
			pumpOn = false;
			drainOn = false;
		}
	}
}
