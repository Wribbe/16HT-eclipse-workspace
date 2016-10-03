package lift;

public class Dicebox {
	
	public float randomFloat(float max) {
		return (float)Math.random()*(max+1);
	}

	public float randomFloat(int max) {
		return randomFloat((float)max);
	}
	
	public int randomDelay(int maxMill) {
		return (int)(1000*randomFloat(maxMill));
	}
}