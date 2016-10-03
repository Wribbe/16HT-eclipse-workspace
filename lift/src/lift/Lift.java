package lift;

public class Lift extends Thread {
	
	private static Monitor monitor;
	private static LiftView view;
	
	public Lift(Monitor monitor, LiftView view) {
		Lift.monitor = monitor;
		Lift.view = view;
	}
	
	public void run() {
		while(true) { 
			try {
				// En monitor metod per trådtyp.
				ElivatorData data = monitor.moveElivator();
				view.moveLift(data.here, data.next);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}