
/**
 * A Simulation Report are all the measured values that
 * are ran within a simulation. They can be combined
 * within an ArrayList to obtain averages.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class SimulationReport
{
	public int numberJobs = 0;
	// Records the state execution history for the system
	public int completedJobs = 0;
	
	// Simulator Clock
	public double clock = 0.0;
	
	// Macintosh Utilization clock when idle
	public double macClock = 0.0;
	
	// NeXTstation Utilization clock when idle
	public double nextClock = 0.0;
	
	// LaserJet Utilization clock when idle
	public double laserClock = 0.0;
	
	// History measures execution times for the jobs
	
	public double jobHistory = 0.0;
	public double macHistory = 0.0;
	public double nextHistory = 0.0;
	public double laserHistory = 0.0;
	
	// Used to help compute L
	public double totalArea;
	public double prevArea;
	public int prevJobTotal;
	public double prevClock;
	
	/**
	 * Every Simulation Report needs to know the number
	 * of jobs in order to proper generate internal averages
	 * @param numberJobs Number of jobs within simulation
	 */
	public SimulationReport ( int numberJobs )
	{
		this.numberJobs = numberJobs;
	}
	
	/**
	 * Computes Macintosh Utilization scores for the simulation
	 * @return Utilization Score
	 */
	public double macUtil ()
	{
		return macHistory / clock;
	}

	/**
	 * Computes NeXTstation Utilization scores for the simulation
	 * @return Utilization Score
	 */
	public double nextUtil ()
	{
		return nextHistory / clock;
	}

	/**
	 * Computes LaserJet Utilization scores for the simulation
	 * @return Utilization Score
	 */
	public double laserUitl ()
	{
		return laserHistory / clock;
	}
	
	/**
	 * Computes the average time for a job to complete
	 * the simulation
	 * @return Average Time 
	 */
	public double averageTime ()
	{
		return jobHistory / numberJobs;
	}
	
	/**
	 * Computes the average number of jobs at a given
	 * time during the simulation
	 * @return Average Number of Jobs
	 */
	public double averageNumberJobs ()
	{
		return (totalArea / clock);
	}
	
	/**
	 * Helper method to help to compute the average number
	 * of jobs within the simulation. Uses the simulator clock to record
	 * the area based on delta job event times within the system
	 * @param currentJobTotal
	 */
	public void updateAverageNumberJobs (int currentJobTotal)
	{
		double area = prevJobTotal * (clock - prevClock);
		totalArea += area;
		
		prevJobTotal = currentJobTotal;
		prevClock = clock;
	}
}
