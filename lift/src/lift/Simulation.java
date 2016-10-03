package lift;

public class Simulation {
	
	private static LiftView view;
	private static Monitor monitor;
	private static TimeCrystal time;
	
	private final static int numPeople = 25;
	private final static int numFloors = 7;
	
	Simulation() {
		view = new LiftView();
		monitor = new Monitor(7, view);
		time = new TimeCrystal(monitor);
	}

	public static void main(String[] args) {
		Simulation sim = new Simulation();
//		time.start();
		Lift lift = new Lift(monitor);
		lift.start();
		
//		for(int i=0; i<numPeople; i++) {
//			Person person = new Person(monitor, numFloors);
//			person.start();
//		}
	}
}