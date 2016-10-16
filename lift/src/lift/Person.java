package lift;

public class Person extends Thread{
	
	private int current;
	private int destination;
	private boolean traveling;
	
	private Monitor monitor;
	private LiftView view;
	
	public Person(Monitor monitor, LiftView view) {
		this.monitor = monitor;
		this.view = view;
		newDestination();
	}
	
	public void newDestination() {
//		D.print("Picking new destination.");
		current = Dicebox.randomFloor();
		destination = Dicebox.randomFloor();
		while(current == destination) {
			destination = Dicebox.randomFloor();
		}
		traveling = false;
		ElevatorData data = monitor.callLiftAt(current);
//		D.print("Want to go from: "+current+" to: "+destination);
		try {
			monitor.animationStart();
		} catch (InterruptedException e) {
		}
		view.drawLevel(current, data.people);
		monitor.animationStop();
	}
	
	public void run() {
		while(true) {
			try {
				// Waiting to enter lift.
				ElevatorData data = monitor.elevatorEvaluation(current, destination, traveling);
				// Entered lift, draw new updated level and lift.
				monitor.animationStart();
				view.drawLevel(data.here, data.people);
				view.drawLift(data.here, data.load);
				monitor.animationStop();
//				monitor.myNotify();
				traveling = true;
				// Waiting for correct floor.
				data = monitor.elevatorEvaluation(current, destination, traveling);
				D.print("Calling exitAnimationRunning() from Person.");
				monitor.animationStart();
				// At coorect floor, draw .. race condition between threads?
				view.drawLift(data.here, data.load);
				monitor.animationStop();
//				monitor.myNotify();
				sleep(500);
				newDestination();
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}