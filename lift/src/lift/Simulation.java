package lift;

public class Simulation {
	
	private LiftView view;
	
	Simulation() {
		view = new LiftView();
	}
	
	private float randomFloat(float max) {
		return (float)Math.random()*(max+1);
	}

	private float randomFloat(int max) {
		return randomFloat((float)max);
	}
	
	private int randomDelay(int maxMill) {
		return (int)(1000*randomFloat(maxMill));
	}

	public static void main(String[] args) {
		Simulation sim = new Simulation();
		for(int i=0; i<20; i++) {
			System.out.println(sim.randomDelay(10));
		}
	}
}