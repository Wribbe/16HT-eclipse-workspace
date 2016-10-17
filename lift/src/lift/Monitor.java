package lift;

public class Monitor {
	
	private int here;
	private int next; private int[] waitEntry;
	private int[] waitExit;
	private int load;
	
	private int direction = 1;
	private final int maxLoad = 4;
	public static final int MAXFLOOORS = 7;
	
	private boolean animationPlaying = false;
	private int animationQueue = 0;
	
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

		while(!elevatorShouldContinue() || animationsInQueue()) {
			wait();
		}
		
		//D.print("Inside getNextFloor(), past wait()");
		ElevatorData data = new ElevatorData();
		data.here = currentLevel();
		data.next = nextFloor();
		next = data.next;

		return data;
	}
	
	public synchronized void animationQueueAdd() {
		animationQueue++;
		D.print("Incrementing queue = "+animationQueue);
	}

	public synchronized void animationQueueDec() {
		animationQueue--;
		D.print("Decrementing queue = "+animationQueue);
	}
	
	public synchronized boolean animationsInQueue() {
		return animationQueue > 0;
	}
	
	public synchronized void animationStart() throws InterruptedException {
		//D.print("In animation start().");
		while (animationsInQueue()) {
			//D.print("Waiting in animation start().");
			wait();
		}
		animationQueueAdd();
		this.animationPlaying = true;	
		//D.print("Setting and notifying in animation start().");
//		notifyAll();
	}
	
	public synchronized boolean animationRunning() {
		return this.animationPlaying;
	}

	public synchronized void animationStop() {
		//D.print("In animationStop().");
		this.animationPlaying = false;	
		animationQueueDec();
		notifyAll();
	}
	
	public synchronized ElevatorData elevatorEvaluation(int current, int destination, boolean traveling) throws InterruptedException {
		
		if (!traveling) { // Waiting for elevator.

//			while (animationRunning() || animationsInQueue() || !roomLeft() || !currentLevelIs(current)) {
			while (!roomLeft() || !currentLevelIs(current) || liftMoving()) {
				wait();
			}
			
			// CRITICAL: Entering elevator, change data.

			load++;
			waitEntry[current]--;
			waitExit[destination]++;

			ElevatorData data = new ElevatorData();

			data.here = current;
			data.load = load;
			data.people = waitEntry[current];
			
//			notifyAll();
			view.drawLevel(here, waitEntry[current]);
			view.drawLift(here, load);
			
			notifyAll();

			return data;

		} else { // Waiting to exit elevator.

//			while (!currentLevelIs(destination) || animationRunning() || animationsInQueue()) {
			while (!currentLevelIs(destination) || liftMoving()) {
				//D.print("Waiting for exit @: "+destination);
				if (currentLevelIs(destination)) {
					//D.print("animation: "+animationRunning());
				}
				wait();
			}

			// CRITICAL: Exiting elevator, modifying data.

			ElevatorData data = new ElevatorData();

			load--;
			waitExit[destination]--;

			data.here = destination;
			data.load = load;

			view.drawLift(data.here, data.load);
			
			notifyAll();

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

	private synchronized int nextFloor() {
		int tempFloor = currentLevel() + direction;
		if (tempFloor >= MAXFLOOORS || tempFloor < 0) {
			direction *= -1; // Switch direction.
		}
		return currentLevel()+direction;
	}
	
	private synchronized boolean peopleWaiting() {
		return waitEntry[currentLevel()] > 0;
	}

	private synchronized boolean peopleExiting() {
		return waitExit[currentLevel()] > 0;
	}
	
	private synchronized boolean roomLeft() {
		return getLoad() < maxLoad;
	}
	
	private synchronized int getLoad() {
		return load;
	}
	
	private synchronized boolean liftMoving() {
		return currentLevel() != next;
	}
	
	private synchronized boolean currentLevelIs(int destination) {
		return currentLevel() == destination;
	}
	
	private synchronized int currentLevel() {
		return here;
	}
	
	private synchronized boolean elevatorShouldContinue() {

		if (peopleExiting()) {
			return false ;
		}

		if (!peopleWaiting() && !peopleExiting()) {
			D.print("No one wants to exit or enter on: "+currentLevel());
			return true;
		}
		if (peopleWaiting() && !roomLeft()) {
			D.print("No room left at: "+currentLevel());
			return true;
		}
		D.print("Default false!");
		return false;
	}
}