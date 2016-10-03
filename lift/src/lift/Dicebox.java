package lift;

public class Dicebox {
	
	public static float randomFloat(float max) {
		return (float)Math.random()*(max+1);
	}

	public static float randomFloat(int max) {
		return randomFloat((float)max);
	}

	public static int randomInt(int max) {
		return (int)randomFloat((float)max);
	}
	
	public static int randomDelay(int maxSec) {
		return (int)(1000*randomFloat(maxSec));
	}
}