package queue;

import java.lang.reflect.WildcardType;
import java.util.LinkedList;
import java.util.List;

class YourMonitor {

	private int nCounters;
	private int waitingCustomers;
	private int lastCustomerServed;
	private List<Integer> avaibleClerks;

	private final int maxCustomers = 100;

	YourMonitor(int n) { 
		nCounters = n;
		waitingCustomers = 0;
		lastCustomerServed = 0;
		avaibleClerks = new LinkedList<Integer>();;
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
		notifyAll();
		return currentWaiting;
	}

	/**
	 * Register the clerk at counter id as free. Send a customer if any. 
	 */
	synchronized void clerkFree(int id) { 
		if (avaibleClerks.indexOf(id) < 0) { // Is not in list.
			avaibleClerks.add(id);
			notifyAll();
		}
	}

	/**
	 * Wait for there to be a free clerk and a waiting customer, then
	 * return the cueue number of next customer to serve and the counter 
	 * number of the engaged clerk.
	 */
	synchronized DispData getDisplayData() throws InterruptedException { 
		while((waitingCustomers == lastCustomerServed) || avaibleClerks.isEmpty()) {
			wait();
		}
		int currentCounter = avaibleClerks.remove(0);
		int currentCustomer = lastCustomerServed;
		lastCustomerServed = (lastCustomerServed+1) % maxCustomers;
		DispData data = new DispData();
		data.counter = currentCounter;
		data.ticket = currentCustomer;
		return data;
	}
}
