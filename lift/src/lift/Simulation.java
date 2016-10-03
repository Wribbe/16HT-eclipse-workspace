package lift;

public class Simulation {
	
	private static LiftView view;
	private static Dicebox dices;
	
	Simulation() {
		view = new LiftView();
		dices = new Dicebox();
	}

	public static void main(String[] args) {
		Simulation sim = new Simulation();
		for(int i=0; i<20; i++) {
			System.out.println(dices.randomDelay(10));
		}
	}
}