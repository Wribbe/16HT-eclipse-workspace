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
class WashingProgram1 extends ExtendedWashingProgram {
	
	public WashingProgram1(AbstractWashingMachine mach,
			double speed,
			TemperatureController tempController,
			WaterController waterController,
			SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	protected void wash() throws InterruptedException {
		
		int preTemperature = 40;
		int temperature = 90;

		int preWashMinutes = 15;
		int washMinutes = 30;

		int rinceMinutes = 2;
		int rinceCycles = 5;
		
		int centrifugeMinutes = 5;
		
		lock();
		
		// Pre-wash.
		fillTo(0.5);
		tempOn(preTemperature);
		slowSpin(preWashMinutes);
		drain();
		
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