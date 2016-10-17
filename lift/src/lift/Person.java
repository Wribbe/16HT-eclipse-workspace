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
	}
	
	public ElevatorData newDestination() {

		traveling = false;

		current = Dicebox.randomFloor();
		destination = Dicebox.randomFloor();
		while(current == destination) {
			destination = Dicebox.randomFloor();
		}
		return monitor.callLiftAt(current);

	}
	
	public void run() {
		while(true) {
			try {

				// Pick destination and draw level.
				ElevatorData data = newDestination(); 	
				view.drawLevel(data.here, data.people);

				// Wait a second before it is possible to enter elevator.
				sleep(1000);

				// Waiting to enter lift.
				data = monitor.elevatorEvaluation(current, destination, traveling);
				// Entered lift.
				traveling = true;
				// Draw new floor state.
				view.drawLevel(data.here, data.people);
				// Draw new lift state.
				view.drawLift(data.here, data.load);
				// Wait slightly so that everyone can't enter at once.
				sleep(500); 
				// Release animation lock.
				monitor.animationDec();

				// Waiting for correct floor.
				data = monitor.elevatorEvaluation(current, destination, traveling);
				// Got out of the lift, draw new lift state.
				view.drawLift(data.here, data.load);
				// Release animaiton lock.
				monitor.animationDec();

				// Wait for max 2 seconds before re-appearing.
				sleep(Dicebox.randomDelay(2));

			} catch (InterruptedException e) {
				break;
			}
		}
	}
}