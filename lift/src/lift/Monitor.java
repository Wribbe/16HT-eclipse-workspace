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
	
	private boolean animationPlaying = false;

	public Monitor(int maxFloors, LiftView view) {
		
		waitEntry = new int[maxFloors];
		waitExit = new int[maxFloors];
		
		next = 0;
		here = 0;
		load = 0;
		view.drawLift(here, load);
	}
	
	public synchronized ElevatorData getNextFloor() throws InterruptedException {

		D.print("Inside getNextFloor()");
		while(!elevatorShouldContinue()) {
			wait();
		}
		
		D.print("Inside getNextFloor(), past wait()");
		ElevatorData data = new ElevatorData();
		data.here = here;
		data.next = nextFloor();
		next = data.next;

		return data;
	}
	
	public synchronized void animationStart() throws InterruptedException {
		while (animationRunning()) {
			wait();
		}
		this.animationPlaying = true;	
		notifyAll();
	}
	
	public synchronized boolean animationRunning() {
		return this.animationPlaying;
	}

	public synchronized void animationStop() {
		this.animationPlaying = false;	
		notifyAll();
	}
	
	public synchronized ElevatorData elevatorEvaluation(int current, int destination, boolean traveling) throws InterruptedException {
		
		if (!traveling) { // Waiting for elevator.

			while (liftMoving() || animationRunning() || !roomLeft()) {
				wait();
			}
			
			// CRITICAL: Entering elevator, change data.

			load++;
			waitEntry[here]--;
			waitExit[destination]++;

			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			data.people = waitEntry[here];
			
			notifyAll();

			return data;

		} else { // Waiting to exit elevator.

			while (liftMoving() || !atDestination(here, destination) || animationRunning()) {
				wait();
			}

			// CRITICAL: Exiting elevator, modifying data.

			ElevatorData data = new ElevatorData();

			load--;
			data.here = here;
			data.load = load;
			data.people = waitEntry[here];

			waitExit[destination]--;

			return data;
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
		int tempFloor = here + direction;
		if (tempFloor >= MAXFLOOORS || tempFloor < 0) {
			direction *= -1; // Switch direction.
		}
		return here+direction;
	}
	
	private synchronized boolean peopleWaiting(int here) {
		return waitEntry[here] > 0;
	}

	private synchronized boolean peopleExiting(int here) {
		return waitExit[here] > 0;
	}
	
	private synchronized boolean roomLeft() {
		return load < maxLoad;
	}
	
	private synchronized boolean liftMoving() {
		return here != next;
	}
	
	private synchronized boolean atDestination(int here, int destination) {
		return here == destination;
	}
	
	private boolean elevatorShouldContinue() {
		if (animationRunning()) {
			return false;
		}
		if (!peopleWaiting(here) && !peopleExiting(here)) {
			return true;
		}
		if (peopleWaiting(here) && !roomLeft()) {
			return true;
		}
		return false;
	}
}