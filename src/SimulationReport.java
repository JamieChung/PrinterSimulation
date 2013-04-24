
public class SimulationReport {

	public int numberJobs = 0;
	// Records the state execution history for the system
	public double clock = 0.0;
	public double macHistory = 0.0;
	public double nextHistory = 0.0;
	public double laserHistory = 0.0;
	
	public double macClock = 0.0;
	public double nextClock = 0.0;
	public double laserClock = 0.0;
	
	public double jobHistory = 0.0;
	
	public SimulationReport ( int numberJobs )
	{
		this.numberJobs = numberJobs;
	}
	
}
