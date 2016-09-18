package todo;
import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem; 
	private static TimeCrystal 	timeCrystal;

	public AlarmClock(ClockInput i, ClockOutput o) {
		input = i;
		output = o;
		timeCrystal = new TimeCrystal(output);
		sem = input.getSemaphoreInstance();
	}

	// The AlarmClock thread is started by the simulator. No
	// need to start it by yourself, if you do you will get
	// an IllegalThreadStateException. The implementation
	// below is a simple alarmclock thread that beeps upon 
	// each keypress. To be modified in the lab.
	public void run() {
		timeCrystal.start();
		while (true) {
			sem.take();
			int currentChoice = input.getChoice(); 
			int currentValue = input.getValue();
			if (currentChoice == ClockInput.SET_TIME) {
				timeCrystal.setTime(currentValue);
			} else if (currentChoice == ClockInput.SET_ALARM) {
			}
		}
	}
}
