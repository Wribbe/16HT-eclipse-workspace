/*
 * Real-time and concurrent programming course, laboratory 3
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980812 Created
 * PP 990924 Revised
 */

package todo;

import done.*;
import se.lth.cs.realtime.event.RTEvent;

/**
 * Program 3 of washing machine. Does the following:
 * <UL>
 *   <LI>Switches off heating
 *   <LI>Switches off spin
 *   <LI>Pumps out water
 *   <LI>Unlocks the hatch.
 * </UL>
 */
abstract class ExtendedWashingProgram extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param   mach             The washing machine to control
	 * @param   speed            Simulation speed
	 * @param   tempController   The TemperatureController to use
	 * @param   waterController  The WaterController to use
	 * @param   spinController   The SpinController to use
	 */
	public ExtendedWashingProgram(AbstractWashingMachine mach,
			double speed,
			TemperatureController tempController,
			WaterController waterController,
			SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}
	
	// ---------------------------------------------------------- PUBLIC METHODS
	
	// Locking manipulation.
	protected void lock() {
		myMachine.setLock(true);
	}
	
	protected void unlock() {
		if (waterInDrum()) {
			drain();
		}
		myMachine.setLock(false);
	}
	
	// Water fill/drain.
	protected void fillTo(double volume) {
		myWaterController.putEvent(new WaterEvent(this,
				WaterEvent.WATER_FILL,
				volume));
		mailbox.doFetch(); // Wait for Ack
	}

	protected void drain() {
		tempOff();
		inletOff();
		myWaterController.putEvent(new WaterEvent(this,
				WaterEvent.WATER_DRAIN,
				0.0));
		mailbox.doFetch(); // Wait for Ack
	}
	
	
	// Spin methods.
	protected void slowSpin(int minutes) {
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
		sleep(waitTime(minutes));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		mailbox.doFetch(); // Wait for Ack
	}
	
	protected void centrifuge(int centrifugeMinutes) {
		if (waterInDrum()) {
			drain();
		}
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
		sleep(waitTime(centrifugeMinutes));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		mailbox.doFetch(); // Wait for Ack
	}
	
	// Rincing.
	protected void rince(int rinceMinutes, int rinceCycles) {
		int currentCycle = 0;
		while(currentCycle < rinceCycles) {
			// Fill up.
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_FILL,
					0.5));
			mailbox.doFetch(); // Wait for Ack
			sleep(waitTime(rinceMinutes));
			currentCycle++;
			// Drain.
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_DRAIN,
					0.0));
			mailbox.doFetch(); // Wait for Ack
		}
	}
	
	// Temperature related methods.
	protected void tempOff() {
		setTemp(0, TEMPOFF);
	}
	
	protected void tempOn(int temperature) {
		if (!waterInDrum()) {
			interrupt();
		}
		setTemp(temperature, TEMPON);
	}

		
	/**
	 * This method contains the actual code for the washing program. Executed
	 * when the start() method is called.
	 */
	protected abstract void wash() throws InterruptedException;

	/**
	 * Private methods and attributes.
	 */

	private static final boolean TEMPON = true;
	private static final boolean TEMPOFF = false;

	private long waitTime(int minutes) {
		return (long)(1000*60*minutes/mySpeed);
	}
	
	private void setTemp(int temperature, boolean status) {

		int tempStatus = 0;
		if (status == TEMPON) {
			tempStatus = TemperatureEvent.TEMP_SET;
		} else if (status == TEMPOFF) {
			tempStatus = TemperatureEvent.TEMP_IDLE;
		}

		myTempController.putEvent(new TemperatureEvent(this,
				tempStatus,
				temperature));
		mailbox.doFetch(); // Wait for Ack
	}

	private boolean waterInDrum() {
		myWaterController.putEvent(new WaterEvent(this,
				WaterEvent.WATER_IDLE,
				0.0));
		WaterEvent event = (WaterEvent)mailbox.doFetch(); // Wait for Ack
		return event.getLevel() > 0.0;
	}
	
	private void inletOff() {
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_IDLE,
					0.0));
			mailbox.doFetch(); // Wait for Ack
	}
	
}
