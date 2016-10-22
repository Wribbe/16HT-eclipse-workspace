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
class WashingProgram2 extends ExtendedWashingProgram {
	
	public WashingProgram2(AbstractWashingMachine mach,
			double speed,
			TemperatureController tempController,
			WaterController waterController,
			SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	protected void wash() throws InterruptedException {
		
		int temperature = 60;
		int washMinutes = 30;

		int rinceMinutes = 2;
		int rinceCycles = 5;
		
		int centrifugeMinutes = 5;
		
		lock();
		
		// Main wash program.
		fillTo(0.5);
		tempOn(temperature);
		slowSpin(washMinutes);
		drain();
		rince(rinceMinutes, rinceCycles);
		centrifuge(centrifugeMinutes);

		unlock();
	}
}
