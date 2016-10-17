package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class SpinController extends PeriodicThread {
	
	private int mode = SpinEvent.SPIN_OFF;

	private AbstractWashingMachine machine;
	
	private int elapsedSeconds = 0;
	private boolean counterRunning = false;
	private int previousSpin;
	private int currentSpin;
	private int interval = 60;

	
	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // 1/sec.
		this.machine = mach;
		previousSpin = AbstractWashingMachine.SPIN_LEFT;
		currentSpin = AbstractWashingMachine.SPIN_RIGHT;
	}

	private void replyToEvent(SpinEvent event) {
		int mode = event.getMode();
		SpinEvent replyEvent = new SpinEvent(this, mode);
		((RTThread)event.getSource()).putEvent(replyEvent);
	}
	
	private void switchSpin() {
		if (mode != SpinEvent.SPIN_FAST) {
			System.out.println("Switching sides.");
			int temp = previousSpin;
			previousSpin = currentSpin;
			currentSpin = temp;
		}
	}
	
	public void perform() {

		SpinEvent event = (SpinEvent) this.mailbox.tryFetch();

		if (event != null) { // Got event.
			mode = event.getMode();
			if (mode == SpinEvent.SPIN_OFF) { // Turn off.
				machine.setSpin(SpinEvent.SPIN_OFF);
				counterRunning = false;
				elapsedSeconds = 0;
				replyToEvent(event);
			} else { // Start.
				if (!counterRunning) {
					counterRunning = true;
				}
				machine.setSpin(currentSpin);
			}
		}		
		
		if (elapsedSeconds >= interval) {
			switchSpin();
			machine.setSpin(currentSpin);
			elapsedSeconds %= interval;
		}
		
		if (counterRunning) {
			elapsedSeconds++;
		}
	}
}
