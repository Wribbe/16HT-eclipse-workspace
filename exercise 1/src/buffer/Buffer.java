package buffer;

import se.lth.cs.realtime.semaphore.*;

/**
 * The buffer.
 */
class Buffer {
	Semaphore mutex; // For mutual exclusion blocking.
	Semaphore free; // For buffer full blocking.
	Semaphore avail; // For blocking when no data is available.

	final static int bufferSize = 8;
	final static boolean DEBUG = true;
	int currentIndex = 0;
	String[] buffData; // The actual buffer.

	Buffer() {
		mutex = new MutexSem();
		free = new CountingSem(bufferSize);
		avail = new CountingSem();
		buffData = new String[bufferSize];  
	}
	
	private int nextPos() {
		int current = currentIndex;
		currentIndex = (currentIndex+1) % bufferSize;
		return current;
	}
	
	private static void debug(String message) {
		if (DEBUG) {
			System.out.println("[?] DEBUG: "+message);
		}
	}

	void putLine(String input) { // Used by producer.
		debug("putLine waiting for free.");
		free.take(); // Wait for buffer empty.
		debug("putLine has free.");
		debug("putLine waiting for mutex.");
		mutex.take(); // Wait for exclusive access.
		debug("putLine has mutex.");
		debug("Got free in putLine.");
		buffData[nextPos()] = new String(input);
		mutex.give(); // Allow others to access.
		debug("putLine has returned mutex.");
		avail.give(); // Allow others to get line.
		debug("putLine has returned avail.");
	}

	String getLine() { // Used by consumer.
		// Exercise 2 ...
		// Here you should add code so that if the buffer is empty, the
		// calling process is delayed until a line becomes available.
		// A caller of putLine hanging on buffer full should be released.
		// ...
		debug("getLine() is waiting for avail.");
		avail.take(); // Wait for data.
		debug("getLine() has got avail.");
		debug("getLine() is waiting for mutex.");
		mutex.take(); // Mutual exclusion.
		debug("getLine() has got mutex.");
		String ans = buffData[currentIndex]; // Get the data.
		buffData[currentIndex] = null; // Reset buffer element.
		nextPos(); // Advance position in buffer.
		mutex.give(); // Return mutual exclusion.
		free.give();  // Signal that the buffer is free.
		return ans;
	}
}
