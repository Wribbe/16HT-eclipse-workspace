/*
 * Real-time and concurrent programming course, laboratory 3
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980812 Created
 * PP 990924 Revised
 */

package todo;

import done.*;

/**
 * Program 3 of washing machine. Does the following:
 * <UL>
 *   <LI>Switches off heating
 *   <LI>Switches off spin
 *   <LI>Pumps out water
 *   <LI>Unlocks the hatch.
 * </UL>
 */
class WashingProgram1 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param   mach             The washing machine to control
	 * @param   speed            Simulation speed
	 * @param   tempController   The TemperatureController to use
	 * @param   waterController  The WaterController to use
	 * @param   spinController   The SpinController to use
	 */
	public WashingProgram1(AbstractWashingMachine mach,
			double speed,
			TemperatureController tempController,
			WaterController waterController,
			SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}
	
	// ---------------------------------------------------------- PUBLIC METHODS
	
	private long waitTime(int minutes) {
		return (long)(1000*60*minutes/mySpeed);
	}

	/**
	 * This method contains the actual code for the washing program. Executed
	 * when the start() method is called.
	 */
	protected void wash() throws InterruptedException {

		// Wash cycle.
//		int minutes = 30;
		int washMinutes = 10;

		// Rince cycle.
		int rinceMinutes = 2;
		int rinceMax = 5;
		int currentRince = 0;
		
		// Centrifuge cycle.
		int centrifugeMinutes = 5;
		
		// Lock.
		myMachine.setLock(true);
		
		// 1/2 machine = 10L = 0.5.
		
		// Fill with water. 
		myWaterController.putEvent(new WaterEvent(this,
				WaterEvent.WATER_FILL,
				0.5));
		mailbox.doFetch(); // Wait for Ack
		
		// Set temperature.
		myTempController.putEvent(new TemperatureEvent(this,
				TemperatureEvent.TEMP_SET,
				30.0));
		mailbox.doFetch(); // Wait for Ack
		

		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
		sleep(waitTime(washMinutes));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		mailbox.doFetch(); // Wait for Ack
		
		// Turn of temperature.
		myTempController.putEvent(new TemperatureEvent(this,
				TemperatureEvent.TEMP_IDLE,
				00.0));
		mailbox.doFetch(); // Wait for Ack
		
		// Drain
		myWaterController.putEvent(new WaterEvent(this,
				WaterEvent.WATER_DRAIN,
				0.0));
		mailbox.doFetch(); // Wait for Ack
		
		while(currentRince < rinceMax) {
			// Fill up.
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_FILL,
					0.5));
			mailbox.doFetch(); // Wait for Ack
			sleep(waitTime(rinceMinutes));
			currentRince++;
			// Drain.
			myWaterController.putEvent(new WaterEvent(this,
					WaterEvent.WATER_DRAIN,
					0.0));
			mailbox.doFetch(); // Wait for Ack
		}
		
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
		sleep(waitTime(centrifugeMinutes));
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		mailbox.doFetch(); // Wait for Ack

		// Lock.
		myMachine.setLock(false);
	}
}
