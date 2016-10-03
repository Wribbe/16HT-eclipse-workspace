package lift;

public class Lift extends Thread {
	
	private Monitor monitor;
	private LiftView view;
	
	public Lift(Monitor monitor, LiftView view) {
		this.monitor = monitor;
		this.view = view;
	}
	
	public void run() {
		while(true) { 
			try {
				ElevatorData data = monitor.moveElevator();
				view.moveLift(data.here, data.next);
				monitor.setNewFloor(data.next);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}