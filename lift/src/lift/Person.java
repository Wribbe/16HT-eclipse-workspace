package lift;

public class Person extends Thread{
	
	private int currentFloor;
	private int nextFloor;
	private int maxFloors;
	
	private Monitor monitor;
	
	public Person(Monitor monitor, int maxFloors) {
		this.maxFloors = maxFloors;
		currentFloor = randomFloor();
		nextFloor = randomFloor();
		this.monitor = monitor;
		try {
			Thread.sleep(Dicebox.randomDelay(45));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		newDestination();
	}
	
	private int randomFloor() {
		return Dicebox.randomInt(maxFloors);
	}
	
	public void newDestination() {
		while(nextFloor == currentFloor) {
			nextFloor = randomFloor();
		}
		D.print("CurrentFloor: "+currentFloor+" Next: "+nextFloor);
		monitor.callLift(currentFloor);
		D.print("Called lift to "+currentFloor);
	}

	public void run() {
		while(true) {
			try {
				if (monitor.atFloor() != currentFloor) {
					wait();
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}