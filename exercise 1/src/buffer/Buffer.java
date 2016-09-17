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

	void putLine(String input) {
		System.out.println("Waitning for free.");
		free.take(); // Wait for buffer empty.
		System.out.println("Free taken.");
		System.out.println("Waitning for mutex.");
		mutex.take(); // Wait for exclusive access.
		System.out.println("Mutex taken.");
		System.out.println("New buffer.");
		buffData[nextPos()] = new String(input);
		System.out.println("Getting rid of mutex.");
		mutex.give(); // Allow others to access.
		System.out.println("Mutex gone.");
		System.out.println("Getting rid of avail.");
		avail.give(); // Allow others to get line.
		System.out.println("Avail gone.");
	}

	String getLine() {
		// Exercise 2 ...
		// Here you should add code so that if the buffer is empty, the
		// calling process is delayed until a line becomes available.
		// A caller of putLine hanging on buffer full should be released.
		// ...
		avail.take(); // Wait for data.
		mutex.take(); // Mutual exclusion.
		String ans = buffData[currentIndex]; // Get the data.
		buffData[currentIndex] = null; // Reset buffer element.
		nextPos(); // Advance position in buffer.
		mutex.give(); // Return mutual exclusion.
		free.give();  // Signal tkat the buffer is free.
		return ans;
	}
}
