package todo;
import done.*;
import se.lth.cs.realtime.semaphore.Semaphore;
import se.lth.cs.realtime.semaphore.MutexSem;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem; 
	private static TimeCrystal 	time;

	public AlarmClock(ClockInput i, ClockOutput o) {
		input = i;
		output = o;
		time = new TimeCrystal(output);
		sem = input.getSemaphoreInstance();
	}

	// The AlarmClock thread is started by the simulator. No
	// need to start it by yourself, if you do you will get
	// an IllegalThreadStateException. The implementation
	// below is a simple alarmclock thread that beeps upon 
	// each keypress. To be modified in the lab.
	public void run() {
		time.run();
		while (true) {
			sem.take();
			output.doAlarm();
		}
	}
}
