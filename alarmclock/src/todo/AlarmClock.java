package todo;
import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem; 
	private static TimeCrystal 	timeCrystal;
	private Storage storage;

	public AlarmClock(ClockInput i, ClockOutput o) {

		/* Set up input, output and button-semaphore. */
		input = i;
		output = o;
		sem = input.getSemaphoreInstance();
		storage = new Storage(output);

		/* Set up TimeCrystal. */
		timeCrystal = new TimeCrystal(storage);
	}

	// The AlarmClock thread is started by the simulator. No
	// need to start it by yourself, if you do you will get
	// an IllegalThreadStateException. The implementation
	// below is a simple alarmclock thread that beeps upon 
	// each keypress. To be modified in the lab.
	public void run() {
		timeCrystal.start();
		int previousChoice = -1;
		while (true) {
			sem.take();
			storage.setAlarmOff();
			int currentChoice = input.getChoice(); 
			if (currentChoice != previousChoice) {
				int currentValue = input.getValue();
				if (previousChoice == ClockInput.SET_TIME) {
					storage.setTime(currentValue);
				} else if (previousChoice == ClockInput.SET_ALARM) {
					storage.setAlarm(currentValue);
				}
			}
			storage.setAlarmFlag(input.getAlarmFlag());
			previousChoice = currentChoice;
		}
	}
}
