
public class SimulationReport
{
	public int numberJobs = 0;
	// Records the state execution history for the system
	
	public int completedJobs = 0;
	
	public double clock = 0.0;
	public double macHistory = 0.0;
	public double nextHistory = 0.0;
	public double laserHistory = 0.0;
	
	public double macClock = 0.0;
	public double nextClock = 0.0;
	public double laserClock = 0.0;
	
	public double jobHistory = 0.0;
	
	
	// Used to help compute L
	public double totalArea;
	public double prevArea;
	public int prevJobTotal;
	public double prevClock;
	
	public SimulationReport ( int numberJobs )
	{
		this.numberJobs = numberJobs;
	}
	
	public double macUtil ()
	{
		return macHistory / clock;
	}
	
	public double nextUtil ()
	{
		return nextHistory / clock;
	}
	
	public double laserUitl ()
	{
		return laserHistory / clock;
	}
	
	public double averageTime ()
	{
		return jobHistory / numberJobs;
	}
	
	public double averageNumberJobs ()
	{
		return (totalArea / clock);
	}
	
	public void updateAverageNumberJobs (int currentJobTotal)
	{
		double area = prevJobTotal * (clock - prevClock);
		totalArea += area;
		
		prevJobTotal = currentJobTotal;
		prevClock = clock;
	}
}
