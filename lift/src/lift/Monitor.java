package lift;

public class Monitor {
	
	private int here;
	private int next; private int[] waitEntry;
	private int[] waitExit;
	private int load;
	
	private int direction = 1;
	private final int maxLoad = 4;
	public static final int MAXFLOOORS = 7;
	
	private LiftView view;

	public Monitor(int maxFloors, LiftView view) {
		
		waitEntry = new int[maxFloors];
		waitExit = new int[maxFloors];
		
		next = 0;
		here = 0;
		load = 0;
		view.drawLift(here, load);
		this.view = view;
	}
	
	public synchronized ElevatorData getNextFloor() throws InterruptedException {

		while(!elevatorShouldContinue()) {
			wait();
		}
		
		ElevatorData data = new ElevatorData();
		data.here = currentLevel();
		data.next = nextFloor();
		next = data.next;

		return data;
	}
	
	public synchronized void elevatorEvaluation(int current, int destination, boolean traveling) throws InterruptedException {
		
		if (!traveling) { // Waiting for elevator.

			while (!roomLeft() || !currentLevelIs(current) || liftMoving()) {
				wait();
			}
			
			// CRITICAL: Entering elevator, modifying shared data.

			load++;
			waitEntry[current]--;
			waitExit[destination]++;

			view.drawLevel(here, waitEntry[current]);
			view.drawLift(here, load);
			notifyAll();

			return;

		} else { // Waiting to exit elevator.

			while (!currentLevelIs(destination) || liftMoving()) {
				wait();
			}

			// CRITICAL: Exiting elevator, modifying shared data.

			load--;
			waitExit[destination]--;

			view.drawLift(here, load);
			notifyAll();

			return;
		}
	}
	
	public synchronized ElevatorData callLiftAt(int floor) {
		waitEntry[floor]++;
		ElevatorData data = new ElevatorData();
		data.people = waitEntry[floor];
		return data;
	}
	
	public synchronized void setNewFloor(int newFloor) {
		here = newFloor;
		notifyAll();
	}

	private int nextFloor() {
		int tempFloor = currentLevel() + direction;
		if (tempFloor >= MAXFLOOORS || tempFloor < 0) {
			direction *= -1; // Switch direction.
		}
		return currentLevel()+direction;
	}
	
	private boolean peopleWaiting() {
		return waitEntry[currentLevel()] > 0;
	}

	private boolean peopleExiting() {
		return waitExit[currentLevel()] > 0;
	}
	
	private boolean roomLeft() {
		return load < maxLoad;
	}
	
	private boolean liftMoving() {
		return currentLevel() != next;
	}
	
	private boolean currentLevelIs(int destination) {
		return currentLevel() == destination;
	}
	
	private int currentLevel() {
		return here;
	}
	
	private boolean elevatorShouldContinue() {

		if (peopleExiting()) {
			return false ;
		}

		if (!peopleWaiting() && !peopleExiting()) {
			return true;
		}
		if (peopleWaiting() && !roomLeft()) {
			return true;
		}
		return false;
	}
}