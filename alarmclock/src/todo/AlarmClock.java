package todo;
import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem; 
	private static TimeCrystal 	timeCrystal;

	public AlarmClock(ClockInput i, ClockOutput o) {

		/* Set up input, output and button-semaphore. */
		input = i;
		output = o;
		sem = input.getSemaphoreInstance();

		/* Set up TimeCrystal. */
		timeCrystal = new TimeCrystal(output);
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
			timeCrystal.buttonPushed();
			if (currentChoice == ClockInput.SET_TIME) {
				timeCrystal.setTime(currentValue);
			} else if (currentChoice == ClockInput.SET_ALARM) {
				timeCrystal.setAlarm(currentValue);
			}
		}
	}
}
