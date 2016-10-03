package lift;

public class Monitor {
	
	private int here;
	private int next;
	private int[] waitEntry;
	private int[] waitExit;
	private int load;
	
	private static final int up = 1;
	private static final int down = -1;

	private static final int open =  1;
	private static final int moving =  0;
	private static final int closed = -1;
	
	private static int maxFloors;
	private static LiftView view;
	
	private boolean liftMoving = false;
	
	public Monitor(int maxFloors, LiftView view) {
		this.maxFloors = maxFloors;
		this.view = view;
		next = 1;
		here = 0;
		view.drawLift(here, 0);
	}
	
	public synchronized ElivatorData moveElivator() throws InterruptedException {
		while(here == next || liftMoving) {
			wait();
		}
		liftMoving = true;
		ElivatorData data = new ElivatorData();
		data.here = here;
		data.next = next;
		return data;
	}
	
	public synchronized void callLift(int floor) {
		next = floor;
	}
	
	public synchronized void arrivedAt(int floor) {
		D.print("Arrived at: "+floor);
		view.drawLift(floor, 0);
		here = floor;
		liftMoving = false;
		callLift((floor + 1)%maxFloors);
	}
}