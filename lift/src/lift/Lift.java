package lift;

public class Lift extends Thread {
	
	private int id;
	private static Dicebox dice;
	
	public Lift(int id) {
		this.id = id;
		dice = new Dicebox();
	}
	
	public void run() {
		for(int i = 0; i < 5; i++) {
			System.out.println("Lift: "+id+" spin "+i);
			try {
				Thread.sleep(dice.randomDelay(10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}