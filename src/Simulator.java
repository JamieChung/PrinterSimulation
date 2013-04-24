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
	private JobManager jobs = new JobManager();
	
	// Records the job history within the system
	private HashMap<Double, Integer> history = new HashMap<Double,Integer>();
	
	// Records the state execution history for the system
	public static double macHistory;
	public static double nextHistory;
	public static double laserHistory;
	
	
	private EventManager events = new EventManager();
	public static double macClock = 0.0;
	public static double nextClock = 0.0;
	public static double laserClock = 0.0;

	/**
	 * Begins the simulation.
	 */
	public void run() {
		
		// Simulate one job
//		jobs.insert());
//		jobs.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, Simulator.clock));
//		jobs.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, Simulator.clock));
		
//		System.out.println("System Clock:" + Simulator.clock);
		events.insert(new Job(JobSource.PCGROUP1, JobState.INITIALIZED, Simulator.clock));
		events.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, Simulator.clock));
		events.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, Simulator.clock));
//		System.out.println(events.events);
		
		/**
		 * Conditions for running simulation
		 * 1. We have a Job.incremental_id less than and equal to the total number of jobs in the system
		 * 2. Our job with id = NUMBER_JOBS + NUMBER_JOBS_WARMUP has finished terminating
		 */
		
		Job j;
		int completeCount = 0;
		while ( completeCount <= (Constants.NUMBER_JOBS_WARMUP + Constants.NUMBER_JOBS) )
		{
			// Find earliest job 
			j = events.getFirstJob();
			if ( j == null ) break;

			Simulator.clock = j.getArrivalTime();
//			System.out.println("Next: " + j);
//			System.out.println("System clock at: "+Simulator.clock);
//			System.out.println(events.events);
			
			switch ( j.getJobState() )
			{
				case INITIALIZED:
					
					// Insert a new job
					events.insert(new Job(j.getJobSource(), JobState.INITIALIZED, Simulator.clock));
					
					// Promote to MAC state
					events.remove(j);
					j.state = JobState.MACINTOSH;
					events.insert(j);
					
//					System.out.println("Job #" + j.id + " Arrived at MAC STATE at " + j.arrivalTime);
				break;
				
				case MACINTOSH:
					events.remove(j);
					
					// Promote to completion and set arrival times based on when jobs will fire
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_MACINTOSH);
					j.arrivalTime += Simulator.macClock + j.executionTime;
					Simulator.macHistory += j.executionTime;
					Simulator.macClock = j.arrivalTime;
					completeCount++;
//					j.state = JobState.COMPLETED;
//					events.insert(j);
					
//					System.out.println("Promoted Job #" + j.id + " to NEXT at " + j.arrivalTime);
				break;
				
				case MACINTOSH_FINISHED:
					
					// Promote to MAC state
					events.remove(j);
//					j.state = JobState.NEXTSTATION;
//					events.insert(j);
					
					break;
				
				case NEXTSTATION:
					
					events.remove(j);
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION);
					j.arrivalTime += Simulator.nextClock + j.executionTime;
					Simulator.nextHistory += j.executionTime;
					Simulator.nextClock = j.arrivalTime;
					j.state = JobState.NEXTSTATION_FINISHED;
					events.insert(j);
					
//					System.out.println("Promoted Job :" + j.id + " to COMPLETION at " + j.arrivalTime);
					
					break;
					
				case NEXTSTATION_FINISHED:
					
					events.remove(j);
					
					completeCount++;
					
					if ( completeCount % 10.0 == 0.0 )
					{
						System.out.println("Completed: " + completeCount + " Jobs : " + Job.incremental_id);
					}
					
					
//					int count = 0;
//					for ( int i = 0; i < events.events.size(); i++ )
//					{
//						
//						if ( count >= 10 ) break;
//						
//						if ( events.events.get(i).getJobState() == JobState.LASERJET )
//						{
//							count++;
//						}
//					}
//					
//					if ( count < 10 )
//					{
//						j.state = JobState.LASERJET;
//						events.insert(j);
//					}
					
					break;
					
				case LASERJET:
					
					events.remove(j);
					
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_LASERJET);
					j.arrivalTime += Simulator.laserClock + j.executionTime;
					Simulator.laserHistory += j.executionTime;
					Simulator.laserClock = j.arrivalTime;
					
					j.state = JobState.COMPLETED;
					events.insert(j);
					
	
					break;
			}
		}

		
//		System.out.println(events.events);
		System.out.println("\n\nSimulator Clock "+Simulator.clock);
		System.out.println("Mac Utilization: " + (Simulator.macHistory/Simulator.clock));
		System.out.println("Next Utilization: " + (Simulator.nextHistory/Simulator.clock));
		System.out.println("Laser Utilization: " + (Simulator.laserHistory/Simulator.clock));
		System.out.println("Total Jobs: "+events.events.size());
		System.out.println("Complete Count: "+completeCount);
	}
	
	/**
	 * Records the system clock and the number of jobs within the system.
	 */
	private void recordHistory ()
	{
		history.put(Double.valueOf(Simulator.clock), jobs.totalJobs());
	}
}
