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
		traveling = false;

		current = Dicebox.randomFloor();
		destination = Dicebox.randomFloor();
		while(current == destination) {
			destination = Dicebox.randomFloor();
		}

		ElevatorData data = monitor.callLiftAt(current);
		view.drawLevel(current, data.people);
	}
	
	public void run() {
		while(true) {
			try {
				// Waiting to enter lift.
				monitor.elevatorEvaluation(current, destination, traveling);
				// Entered lift.
				traveling = true;
				// Waiting for correct floor.
				monitor.elevatorEvaluation(current, destination, traveling);
				// Sleep for max 2 seconds before re-appearing.
				sleep(Dicebox.randomDelay(2));
				newDestination(); 	// Pick new destination.
				sleep(1000); 		// Wait a second before possible to enter elevator.
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}