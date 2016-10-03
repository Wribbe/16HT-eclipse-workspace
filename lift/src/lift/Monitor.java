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
			wait();
		}
		if (traveling) { // In elevator.
			while(here != destination) {
				wait();
			}
			load--; // Exiting elevator;
			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			return data;
		} else { // On a floor.
			while(here != current) {
				wait();
			}
			while(load >= maxLoad) {
				wait();
			}
			load++; // Entering elevator.
			waitEntry[here]--;
			ElevatorData data = new ElevatorData();
			data.here = here;
			data.load = load;
			data.people = waitEntry[here];
			return data;
		}
	}
	
	public synchronized ElevatorData callLiftAt(int floor) {
		D.print(""+floor);
		waitEntry[floor]++;
		ElevatorData data = new ElevatorData();
		data.people = waitEntry[floor];
		return data;
	}

	public synchronized void setNewFloor(int newFloor) {
		here = newFloor;
		notifyAll();
	}
}