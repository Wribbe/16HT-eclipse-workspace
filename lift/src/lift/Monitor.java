package lift;

public class Monitor {
	
	private int here;
	private int next; private int[] waitEntry;
	private int[] waitExit;
	private int load;
	
	private int direction = 1;
	private final int maxLoad = 4;
	public static final int MAXFLOOORS = 7;
	
	private int animationQueue = 0;

	public Monitor(int maxFloors, LiftView view) {
		
		waitEntry = new int[maxFloors];
		waitExit = new int[maxFloors];
		
		next = 0;
		here = 0;
		load = 0;
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
	
	
	private void animationAdd() {
		animationQueue++;
	}

	public synchronized void animationDec() {
		animationQueue--;
		notifyAll();
	}
	
	public synchronized boolean animationsInQueue() {
		return animationQueue > 0;
	}
	
	public synchronized ElevatorData elevatorEvaluation(int current, int destination, boolean traveling) throws InterruptedException {
		
		if (!traveling) { // Waiting for elevator.

			while (!roomLeft() || !currentLevelIs(current) || liftMoving() || animationsInQueue()) {
				wait();
			}
			
			// CRITICAL: Entering elevator, modifying shared data.

			animationAdd();

			load++;
			waitEntry[current]--;
			waitExit[destination]++;
			
			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			data.people = waitEntry[current];
			
			return data;

		} else { // Waiting to exit elevator.

			while (!currentLevelIs(destination) || liftMoving() || animationsInQueue()) {
				wait();
			}

			// CRITICAL: Exiting elevator, modifying shared data.

			animationAdd();

			load--;
			waitExit[destination]--;

			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;

			return data;
		}
	}
	
	public synchronized ElevatorData callLiftAt(int floor) {
		waitEntry[floor]++;
		ElevatorData data = new ElevatorData();
		data.people = waitEntry[floor];
		data.here = floor;
		notifyAll();
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
	
	private int peopleWaitingAt(int floor) {
		return waitEntry[floor];
	}

	private boolean peopleWaiting() {
		return peopleWaitingAt(currentLevel()) > 0;
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
	
	private boolean noPeopleWaitingOrTraveling() {

		// Check elevator for passengers.
		if (load > 0) {
			return false;
		}

		// Check all floor for people waiting.
		for (int i=0; i<MAXFLOOORS; i++) {
			if(peopleWaitingAt(i) > 0) {
				return false;
			}
		}

		return true;
	}
	
	private boolean elevatorShouldContinue() {
		if (animationsInQueue() || peopleExiting() || (peopleWaiting() && roomLeft()) || noPeopleWaitingOrTraveling()) {
			return false;
		}
		return true;
	}
}