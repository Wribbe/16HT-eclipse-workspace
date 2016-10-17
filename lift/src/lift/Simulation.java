package lift;

public class Simulation {
	
	private static LiftView view;
	private static Monitor monitor;
	
	private final static int numPeople = 15;
	
	Simulation() {
		view = new LiftView();
		monitor = new Monitor(7, view);
	}

	public static void main(String[] args) {
		new Simulation();
		
		for(int i=0; i<numPeople; i++) {
			Person person = new Person(monitor, view);
			person.start();
		}

		Lift lift = new Lift(monitor, view);
		lift.start();
	}
}