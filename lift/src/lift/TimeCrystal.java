package lift;

public class TimeCrystal extends Thread {
	
	private Monitor monitor;
	
	public TimeCrystal(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void run() {
		while(true) {
			try {
				monitor.newTick();
				sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}