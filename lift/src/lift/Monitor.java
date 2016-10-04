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
		while(waitExit[here] > 0) {
			wait();
		}
		ElevatorData data = new ElevatorData();
		data.here = here;
		data.next = next;
		return data;
	}
	
	public synchronized ElevatorData getNextFloor() throws InterruptedException {
		while(!elevatorShouldContinue()) {
			wait();
		}
		ElevatorData data = new ElevatorData();
		data.here = here;
		data.next = nextFloor();
		return data;
	}
	
	public synchronized ElevatorData elevatorEvaluation(int current, int destination, boolean traveling) throws InterruptedException {
		while(here != next) { // Lift moving.
			D.print("Lift is moving.");
			wait();
		}
		ElevatorData data = new ElevatorData();
		if (!traveling && current == here && load <= maxLoad) { // Lift here and has room.
			load++;
			waitEntry[here]--;
			waitExit[destination]++;
		} else if (traveling && here == destination) {
			load--;
			waitExit[destination]--;
		} else { // Lift not at correct position.
			wait();
		}
		data.here = here;
		data.load = load;
		data.people = waitEntry[here];
		return data;
	}
	
	public synchronized ElevatorData callLiftAt(int floor) {
		waitEntry[floor]++;
		ElevatorData data = new ElevatorData();
		data.people = waitEntry[floor];
		notifyAll();
		return data;
	}
	
	public synchronized void setNewFloor(int newFloor) {
		here = newFloor;
		notifyAll();
	}

	private int nextFloor() {
		int tempFloor = here + direction;
		if (tempFloor >= MAXFLOOORS || tempFloor < 0) {
			direction *= -1; // Switch direction.
		}
		return here+direction;
	}
	
	private boolean elevatorShouldContinue() {
		boolean peopleWaiting = waitEntry[here] > 0;
		boolean peopleExiting = waitExit[here] > 0;
		boolean roomLeft = load <= maxLoad;
		if (!peopleWaiting && !peopleExiting) {
			return true;
		} else if (peopleWaiting && !roomLeft) {
			return true;
		}
		return false;
	}
}