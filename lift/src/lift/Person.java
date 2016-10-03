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
		current = Dicebox.randomFloor();
		destination = Dicebox.randomFloor();
		while(current == destination) {
			destination = Dicebox.randomFloor();
		}
		traveling = false;
		ElivatorData data = monitor.callLiftAt(current);
		view.drawLevel(current, data.people);
	}
	
	public void run() {
		while(true) {
			try {
				ElivatorData data = monitor.elivatorStatus(current, destination, traveling);
				view.drawLevel(data.here, data.people);
				view.drawLift(data.here, data.load);
				traveling = true;
				data = monitor.elivatorStatus(current, destination, traveling);
				view.drawLift(data.here, data.load);
				newDestination();
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}