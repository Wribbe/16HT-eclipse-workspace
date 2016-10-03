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

		next = 1;
		here = 0;
		load = 0;
		view.drawLift(here, load);
	}
	
	public synchronized ElivatorData moveElivator() throws InterruptedException {
		while(here == next) {
			wait();
		}
		ElivatorData data = new ElivatorData();
		data.here = here;
		data.next = next;
		return data;
	}
	
	public synchronized ElivatorData elivatorStatus(int current, int destination, boolean traveling) throws InterruptedException {
		while(here != next) {
			wait();
		}
		if (traveling) { // In elivator.
			while(here != destination) {
				wait();
			}
			load--; // Exiting elivator;
			ElivatorData data = new ElivatorData();
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
			load++; // Entering elivator.
			waitEntry[here]--;
			ElivatorData data = new ElivatorData();
			data.here = here;
			data.load = load;
			data.people = waitEntry[here];
			return data;
		}
	}
	
	public synchronized ElivatorData callLiftAt(int floor) {
		D.print(""+floor);
		waitEntry[floor]++;
		ElivatorData data = new ElivatorData();
		data.people = waitEntry[floor];
		return data;
	}

	public synchronized void setNewFloor(int newFloor) {
		here = newFloor;
		notifyAll();
	}
}