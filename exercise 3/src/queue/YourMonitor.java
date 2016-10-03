package queue;

import java.lang.reflect.WildcardType;

class YourMonitor {
	private int nCounters;
	private int waitingCustomers;
	private final int maxCustomers = 100;

	YourMonitor(int n) { 
		nCounters = n;
		waitingCustomers = 0;
	}

	/**
	 * Return the next queue number in the intervall 0...99. 
	 * There is never more than 100 customers waiting.
	 */
	synchronized int customerArrived() { 
		/** 
		 * Increment it with a hard stop at maxCustomers. Return the value
		 * before the increment.
		 */
		int currentWaiting = waitingCustomers;
		waitingCustomers = (waitingCustomers + 1) % maxCustomers;
		return currentWaiting;
	}

	/**
	 * Register the clerk at counter id as free. Send a customer if any. 
	 */
	synchronized void clerkFree(int id) { 
		// Implement this method...
	}

	/**
	 * Wait for there to be a free clerk and a waiting customer, then
	 * return the cueue number of next customer to serve and the counter 
	 * number of the engaged clerk.
	 */
	synchronized DispData getDisplayData() throws InterruptedException { 
		// Implement this method...
		return null;
	}
}
