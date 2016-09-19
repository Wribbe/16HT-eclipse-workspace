package todo;

public class TimeCrystal extends Thread {
	
	private final int standardSleep = 1000;
	private long nextCorrectTime;
	private Storage storage;

	public TimeCrystal(Storage storage) {

		/* Set up internal classes. */
		this.storage = storage;
	}
	
	public void run() {
		nextCorrectTime = System.currentTimeMillis();
		while (true) {
			driftcorrectedSleep();
			storage.nextSecond();
		}
	}
	
	private void driftcorrectedSleep() {
		nextCorrectTime += standardSleep;
		nap(standardSleep);
		long diff = nextCorrectTime - System.currentTimeMillis();
		if (diff > 0) {
			nap(diff);
		}
	}
	
	private void nap(long mills) {
		try {
			sleep(mills);
		} catch (InterruptedException e) {
			/* Don't care. */
		}
	}
}