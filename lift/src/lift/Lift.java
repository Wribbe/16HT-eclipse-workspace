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
				ElevatorData data = monitor.getNextFloor();
//				D.print("Got next floor: "+data.next);
				monitor.animationStart();
				view.moveLift(data.here, data.next);
				monitor.animationStop();
				monitor.setNewFloor(data.next);
//				D.print("New floor set: "+data.next);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}