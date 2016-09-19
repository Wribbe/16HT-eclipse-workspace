package todo;
import done.*;

import se.lth.cs.realtime.semaphore.Semaphore;
import se.lth.cs.realtime.semaphore.MutexSem;
import se.lth.cs.realtime.semaphore.CountingSem;

public class TimeCrystal extends Thread {
	
	private final int standardSleep = 1000;
	private long previousTime, currentTime, sleepDelta, timeDiff, currentSleep;
	private ClockOutput output;

	/* Debug printout flag. */
	private boolean DEBUG = false;
	
	/* Time variables: */
	private int time;
	private int hourFac = 10000;
	private int minFac = 100;
	private int timeZone = 2;
	
	/* Alarm variables: */
	private int alarm = -1;
	private int currentAlarm = 0;
	private int maxAlarm = 20;
	private boolean alarmOn = false;
	
	/* Time Crystal Semaphors. */
	private Semaphore timeWrite;
	private Semaphore alarmToggle;
	
	public void buttonPushed() {
		setAlarmOff();
	}

	public TimeCrystal(ClockOutput output) {
		this.output = output;
		sleepDelta = 0;
		time = getCurrentTime();
		currentSleep = standardSleep;
		output.showTime(time);
		
		/* Set up semaphores. */
		timeWrite = new MutexSem();
		alarmToggle = new MutexSem();
	}
	
	public void setTime(int time) {
		/* Set TimeCrystal to specific time. */
		timeWrite.take();
		this.time = time;
		timeWrite.give();
	}
	
	public void setAlarm(int alarmtime) {
		/* Set TimeCrystal alarm variable. */
		alarm = alarmtime;
	}
	
	public void run() {
		while (true) {
			driftcorrectedSleep();
			setTime(nextSecond());
			checkAlarm();
			output.showTime(time);
		}
	}
	
	public void setAlarmOff() {
		/* Turn of alarm. */
		alarmToggle.take();
		alarmOn = false;
		currentAlarm = 0;
		alarmToggle.give();
	}

	public void setAlarmOn() {
		/* Turn on alarm. */
		alarmToggle.take();
		alarmOn = true;
		currentAlarm = 0;
		alarmToggle.give();
	}

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
	
	private int nextSecond() {
		/* Deconstruct the time int into its components. */
		/* Format: int time = hhmmss. */
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
		if (hours > 24) {
			hours = 0;
		}
		
		/* Debug printout message. */
		if (DEBUG) {
			String message = "Time: "+time+
							 " Hours: "+hours+
							 " Minutes: "+minutes+
							 " Seconds: "+seconds;
			System.out.println(message);
		}
		
		/* Re-assemble time variable and return. */
		return assembleTime(hours, minutes, seconds);
	}
	
	private void driftcorrectedSleep() {
		currentTime = System.currentTimeMillis();
		currentSleep = currentSleep + sleepDelta;
		try {
			sleep(currentSleep);
		} catch (InterruptedException e) {
			/* Don't care. */
		}
		previousTime = currentTime;
		currentTime = System.currentTimeMillis();
		timeDiff = currentTime - previousTime;
		sleepDelta = standardSleep - timeDiff;
		if (DEBUG) {
			String message = "Prev: "+previousTime+
							 " Curr: "+currentTime+
							 " Diff: "+timeDiff+
							 " Delta: "+sleepDelta;
			System.out.println(message);
		}
	}
	
	private boolean checkAlarm() {
		/* Check if it's time for an alarm, trigger it if true. */
		if (time == alarm) {
			setAlarmOn();
		}
		if (alarmOn) { 
			currentAlarm++;
			output.doAlarm();
			if (currentAlarm >= maxAlarm) {
				setAlarmOff();
			}
		}
		return false;
	}
}
