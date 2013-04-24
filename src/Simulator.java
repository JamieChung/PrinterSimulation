import java.util.HashMap;


/**
 * Main Simulation which manages the simulation clock and job queues.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Simulator {
	
	// Manages all the jobs within the system
	private JobManager jobs = new JobManager();
	
	// Records the job history within the system
	private HashMap<Double, Integer> history = new HashMap<Double,Integer>();
	
	private EventManager events = new EventManager();

	/**
	 * Begins the simulation.
	 */
	public void run() {
		SimulationReport report;
		
		run(Constants.NUMBER_JOBS_WARMUP);
		report = run(Constants.NUMBER_JOBS);

		double macUtil = (report.macHistory/report.clock);
		double nextUtil = (report.nextHistory/report.clock);
		double laserUtil = (report.laserHistory/report.clock);
		
		System.out.println("Mac Utilization: " + macUtil);
		System.out.println("Next Utilization: " + nextUtil);
		System.out.println("Laser Utilization: " + laserUtil);
		System.out.println("Total Jobs: "+events.events.size());
		
		System.out.println("Job ID: "+Job.incremental_id);
	}
	
	private SimulationReport run ( int number_jobs )
	{
		SimulationReport report = new SimulationReport();
		
		Job.incremental_id = 0;
		events.insert(new Job(JobSource.PCGROUP1, JobState.INITIALIZED, report.clock));
		
		if ( number_jobs > 1 )
		{
			events.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, report.clock));
		}
		
		if ( number_jobs > 2 )
		{
			events.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, report.clock));
		}
		
		/**
		 * Conditions for running simulation
		 * 1. We have a Job.incremental_id less than and equal to the total number of jobs in the system
		 * 2. Our job with id = NUMBER_JOBS + NUMBER_JOBS_WARMUP has finished terminating
		 */
		
		Job j;
		int completeCount = 0;
		int countLaser = 0;
		while ( completeCount <= number_jobs )
		{
			// Find earliest job 
			j = events.getFirstJob();
			if ( j == null ) break;

			report.clock = j.getArrivalTime();
			
//			System.out.println("Next: " + j);
//			System.out.println("System clock at: "+Simulator.clock);
//			System.out.println(events.events);
			
			switch ( j.getJobState() )
			{
				case INITIALIZED:
					// Promote to MAC state
					events.remove(j);
					j.state = JobState.MACINTOSH;
					events.insert(j);
					
//					System.out.println("Job #" + j.id + " to arrive at MAC at " + j.arrivalTime);
				break;
				
				case MACINTOSH:

					if (Job.incremental_id < number_jobs )
					{
						// Insert a new job
						events.insert(new Job(j.getJobSource(), JobState.INITIALIZED, report.clock));
					}
					
					events.remove(j);
					
					// Promote to completion and set arrival times based on when jobs will fire
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_MACINTOSH);
					report.macHistory += j.executionTime;
					j.arrivalTime += j.executionTime;
					
					j.state = JobState.NEXTSTATION;
					events.insert(j);
					
//					System.out.println("Job #" + j.id + " to arrive at NEXT at " + j.arrivalTime);
				break;
				
				case NEXTSTATION:
					
					events.remove(j);
					
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION);
					
					report.nextHistory += j.executionTime;
					j.arrivalTime += j.executionTime;
					j.state = JobState.NEXTSTATION_FINISHED;
					
					events.insert(j);
					
					break;
					
				case NEXTSTATION_FINISHED:
					
					events.remove(j);
					
					if ( countLaser < 10 )
					{
						countLaser++;
						j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_LASERJET);
						report.laserHistory += j.executionTime;
						report.laserClock = j.arrivalTime;
						j.state = JobState.LASERJET;
						events.insert(j);
					}
					else
					{
						completeCount++;
					}
					
					break;
					
				case LASERJET:
					
					events.remove(j);
					completeCount++;
					
					countLaser--;
					if ( countLaser < 0 )
					{
						countLaser = 0;
					}
					
					break;
			}
		}
		
		return report;
	}
	
	/**
	 * Records the system clock and the number of jobs within the system.
	 */
	private void recordHistory ()
	{
//		history.put(Double.valueOf(report.clock), jobs.totalJobs());
	}
}
