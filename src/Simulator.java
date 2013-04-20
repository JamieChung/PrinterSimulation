import java.util.HashMap;


/**
 * Main Simulation which manages the simulation clock and job queues.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Simulator {

	// Simulator Clock
	public static double clock = 0.0;
	
	// Manages all the jobs within the system
	private JobStateMap jobs = new JobStateMap();
	
	// Records the job history within the system
	private HashMap<Double, Integer> history = new HashMap<Double,Integer>();

	/**
	 * Begins the simulation.
	 */
	public void run() {
		
		// Simulate one job
		jobs.insert(new Job(JobSource.PCGROUP1, JobState.INITIALIZED, clock));
		jobs.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, clock));
		jobs.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, clock));
		
		// Update job initialized queue
		while ( clock <= 300.0 )
		{
			for ( JobState state : JobState.values() )
			{
				if ( jobs.canPromote(state, clock) )
				{
					jobs.promote(jobs.getFirst(state));
				}
			}
			
			if ( clock % 10.0 == 0 )
			{
				System.out.println("["+clock+"]");
			}
			
			recordHistory();
			clock += 0.01;
		}
	}
	
	/**
	 * Records the system clock and the number of jobs within the system.
	 */
	private void recordHistory ()
	{
		history.put(Double.valueOf(clock), jobs.totalJobs());
	}
}
