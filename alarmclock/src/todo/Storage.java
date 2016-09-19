package todo;

import done.*;

import se.lth.cs.realtime.semaphore.MutexSem;
import se.lth.cs.realtime.semaphore.Semaphore;

public class Storage {

	/** Class variables. **/
	
	/* Time variables: */
	private int hourFac = 10000;
	private int minFac = 100;
	private int timeZone = 2;
	
	/* Internal classes: */
	private ClockOutput output;
	
	/* Alarm variables: */
	private int alarmtime = -1;
	private int currentAlarmIteration = 0;
	private int maxAlarmIterations = 20;
	
	/* Shared variables: */
	private int time = -1;
	private boolean alarmOn = false;

	/* Semaphores. */
	private Semaphore mutex;
	
	/** Public methods. **/

	public Storage(ClockOutput output) {
		/* Set up semaphores. */
		mutex = new MutexSem();
		
		/* Set up internal classes. */
		this.output = output;
		
		/* Set current time. */
		time = getCurrentTime();
	}
	
	public void setAlarm(int alarmtime) {
		/* Set TimeCrystal alarm variable. */
		mutex.take();
		this.alarmtime = alarmtime;
		mutex.give();
	}
	
	public void setAlarmOff() {
		/* Turn of alarm. */
		mutex.take();
		alarmOn = false;
		currentAlarmIteration = 0;
		mutex.give();
	}

	public void setAlarmOn() {
		/* Turn on alarm. */
		mutex.take();
		alarmOn = true;
		currentAlarmIteration = 0;
		mutex.give();
	}

	public void setTime(int time) {
		/* Set TimeCrystal to specific time. */
		mutex.take();
		this.time = time;
		output.showTime(time);
		mutex.give();
	}
	
	public void nextSecond() {
		/* Deconstruct the time int into its components. */
		/* Format: int time = hhmmss. */
		mutex.take();
		long tempTime = time;
		long hours = tempTime/hourFac;
		tempTime -= hours*hourFac;
		long minutes = tempTime/minFac;
		tempTime -= minutes*minFac;
		long seconds = tempTime;
		
		/* Increment seconds and other components accordingly. */ 
		seconds += 1;
		if (seconds >= 60) {
			seconds %= 60;
			minutes += 1;
		}
		if (minutes >= 60) {
			minutes %= 60;
			hours += 1;
		}
		if (hours >= 24) {
			hours = 0;
		}
		
		/* Re-assemble time variable and return. */
		setTime(assembleTime(hours, minutes, seconds));
		
		/* Check alarm. */
		checkAlarm();
		mutex.give();
	}
	
	/** Private methods. **/

	private int getCurrentTime() {
		/* Calculate the current time based on currentTimeMillis(). */
		long millis = System.currentTimeMillis();
		long seconds = (millis/1000) % 60; 
		long minutes = (millis/(1000 * 60)) % 60;
		long hours = (millis/(1000 * 60 * 60)) % 24;
		
		/* Adjust for timeZone. */
		hours += timeZone;
		
		/* Calculate and return a current time int with correct format. */
		return assembleTime(hours, minutes, seconds);
	}
	
	private int assembleTime(long hours, long minutes, long seconds) {
		int time = 0;
		time += hours * hourFac;
		time += minutes * minFac;
		time += seconds;
		return time;
	}

	private void checkAlarm() {
		/* Check if it's time for an alarm, trigger it if true. */
		if (time == alarmtime) {
			setAlarmOn();
		}
		if (alarmOn) { 
			output.doAlarm();
			if (currentAlarmIteration++ >= maxAlarmIterations) {
				setAlarmOff();
			}
		}
	}
}