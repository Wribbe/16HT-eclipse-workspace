package lift;

public class Lift extends Thread {
	
	private static Monitor monitor;
	private static view;
	
	public Lift(Monitor monitor, LiftView view) {
		this.monitor = monitor;
		this.veiw = view;
	}
	
	public void run() {
		while(true) { 
			try {
				// En monitor metod per trådtyp.
				int next_floor = monitor.nextFloor();
				view.moveLift(here, next);
				sleep(1000);
				monitor.arrivedAt(next_floor);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}