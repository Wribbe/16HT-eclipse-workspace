package lift;

public class Monitor {
	
	private int here;
	private int next;
	private int[] waitEntry;
	private int[] waitExit;
	private int load;
	
	private int direction = 1;
	private final int maxLoad = 4;
	public static final int MAXFLOOORS = 7;
	
	public Monitor(int maxFloors, LiftView view) {
		
		waitEntry = new int[maxFloors];
		waitExit = new int[maxFloors];

		next = 0;
		here = 0;
		load = 0;
		view.drawLift(here, load);
	}
	
	public synchronized ElevatorData moveElevator() throws InterruptedException {
		while(here == next) {
			wait();
		}
		ElevatorData data = new ElevatorData();
		data.here = here;
		data.next = next;
		return data;
	}
	
	public synchronized ElevatorData elevatorStatus(int current, int destination, boolean traveling) throws InterruptedException {
		while(here != next) {
			D.print("Lift is moving");
			wait();
		}
		if (traveling) { // In elevator.
			D.print("Person is traveling");
			while(here != destination) {
				notifyAll();
				wait();
			}
			load--; // Exiting elevator;
			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			shouldElevatorContinue();
			notifyAll();
			return data;
		} else { // On a floor.
			D.print("Person is not traveling");
			while(here != current || load >= maxLoad) {
				D.print("Lift is at: "+here+" not at floor: "+current);
				notifyAll();
				wait();
			}
			D.print("Entering elevator.");
			load++; // Entering elevator.
			waitEntry[here]--;
			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			data.people = waitEntry[here];
			shouldElevatorContinue();
			notifyAll();
			return data;
		}
	}
	
	public synchronized ElevatorData callLiftAt(int floor) {
		waitEntry[floor]++;
		ElevatorData data = new ElevatorData();
		data.people = waitEntry[floor];
		if (here == next) { // Lift is stopped.
			next = floor;
		}
		return data;
	}
	
	public synchronized void setNewFloor(int newFloor) {
		here = newFloor;
		notifyAll();
	}

	private void nextFloor() {
		int tempFloor = here + direction;
		if (tempFloor >= MAXFLOOORS || tempFloor < 0) {
			direction *= -1; // Switch direction.
		}
		next = here+direction;
	}
	
	private void shouldElevatorContinue() {
		if(load >= maxLoad || waitEntry[here] == 0) {
			nextFloor();
		}
	}
}