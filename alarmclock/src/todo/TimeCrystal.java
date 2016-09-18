package todo;
import done.*;

public class TimeCrystal extends Thread {
	
	private final int standardSleep = 1000;
	private long previousTime, currentTime, sleepDelta, timeDiff, currentSleep;
	private ClockOutput output;
	private boolean DEBUG = true;
	private int time = 120000;

	public TimeCrystal(ClockOutput output) {
		this.output = output;
		sleepDelta = 0;
		currentSleep = standardSleep;
		output.showTime(time);
	}
	
	public void driftcorrectedSleep() {
		currentTime = System.currentTimeMillis();
		currentSleep = currentSleep + sleepDelta;
		try {
			sleep(currentSleep);
		} catch (InterruptedException e) {
			/* Don't care. */
		}
		previousTime = currentTime;
		currentTime = System.currentTimeMillis();
		timeDiff = currentTime - previousTime;
		sleepDelta = standardSleep - timeDiff;
		if (DEBUG) {
			String message = "Prev: "+previousTime+
							 " Curr: "+currentTime+
							 " Diff: "+timeDiff+
							 " Delta: "+sleepDelta;
			System.out.println(message);
		}
	}

	public void run() {
		while (true) {
			driftcorrectedSleep();
			output.showTime(++time);
		}
	}
}
