import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Main Simulation which manages the simulation clock and job queues.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Simulator
{
	
	private EventManager events = new EventManager();
	public ArrayList<SimulationReport> reports = new ArrayList<SimulationReport>();

	public String checkBounds ( double x, double lower, double upper )
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(" - ");
		sb.append("[" + lower + ", " + upper + "] ");
		if ( x < lower || x > upper )
		{
			sb.append("BAD");
		}
		
		
		return sb.toString();
	}
	
	/**
	 * Begins the simulation.
	 */
	public void run() {
		
//		Random r = new Random();
//		NumberGenerator.gv_lRandomNumberSeed = r.nextLong();
		
		Random r = new Random();
		
		for ( int i = 0; i <= 30; i++ )
		{
			NumberGenerator.gv_lRandomNumberSeed = r.nextLong();
			run(Constants.NUMBER_JOBS_WARMUP);
			reports.add(run(Constants.NUMBER_JOBS));	
		}
		
		double averageMacUtil = 0.0, averageNextUtil = 0.0, averageLaserUtil = 0.0;
		double averageTime = 0.0, averageNumberJobs = 0.0;
		
		for ( SimulationReport report : reports )
		{
			averageMacUtil += report.macUtil();
			averageNextUtil += report.nextUtil();
			averageLaserUtil += report.laserUitl();
			averageTime += report.averageTime();
			averageNumberJobs += report.averageNumberJobs();
		}
		
		averageMacUtil = (averageMacUtil / reports.size());
		averageNextUtil = (averageNextUtil / reports.size());
		averageLaserUtil = (averageLaserUtil / reports.size());
		averageTime = (averageTime / reports.size());
		averageNumberJobs = (averageNumberJobs / reports.size());
		
		System.out.println("Mac Utilization: " + averageMacUtil + checkBounds(averageMacUtil, Constants.MAC_UTIL_LOWER_VALUE, Constants.MAC_UTIL_UPPER_VALUE));
		System.out.println("Next Utilization: " + averageNextUtil + checkBounds(averageNextUtil, Constants.NEXT_UTIL_LOWER_VALUE, Constants.NEXT_UTIL_UPPER_VALUE));
		System.out.println("Laser Utilization: " + averageLaserUtil + checkBounds(averageLaserUtil, Constants.LASER_UTIL_LOWER_VALUE, Constants.LASER_UTIL_UPPER_VALUE));
		System.out.println("Average Time : " + averageTime + checkBounds(averageTime, Constants.AVERAGE_LOWER_TIME, Constants.AVERAGE_UPPER_TIME));
	}
	
	private SimulationReport run ( int number_jobs )
	{
		SimulationReport report = new SimulationReport(number_jobs);
		
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
					
					// Our projected arrival time is less than when the clock will be idle
					if ( (j.arrivalTime + j.executionTime) < report.macClock )
					{
						j.arrivalTime = report.macClock + j.executionTime;
					}
					else
					{
						j.arrivalTime += j.executionTime;
					}
					
					report.macClock = j.arrivalTime;
					
					j.state = JobState.NEXTSTATION;
					events.insert(j);
					
//					System.out.println("Job #" + j.id + " to arrive at NEXT at " + j.arrivalTime);
				break;
				
				case NEXTSTATION:
					
					events.remove(j);
					
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION);
					report.nextHistory += j.executionTime;

					// Our projected arrival time is less than when the clock will be idle
					if ( (j.arrivalTime + j.executionTime) < report.nextClock )
					{
						j.arrivalTime = report.nextClock + j.executionTime;
					}
					else
					{
						j.arrivalTime += j.executionTime;
					}
					
					report.nextClock = j.arrivalTime;
					
					j.state = JobState.NEXTSTATION_FINISHED;
					events.insert(j);
					
					break;
					
				case NEXTSTATION_FINISHED:
					
					events.remove(j);
					
					if ( countLaser < Constants.MAX_NUMBER_JOBS_PRINTER )
					{
						countLaser++;
						j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_LASERJET);
						report.laserHistory += j.executionTime	;
						
						if ( (j.arrivalTime + j.executionTime) < report.laserClock )
						{
							j.arrivalTime = report.laserClock + j.executionTime;
						}
						else
						{
							j.arrivalTime += j.executionTime;
						}
						
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
					
					report.jobHistory += (j.arrivalTime - j.systemStartTime);
					completeCount++;
					
					if ( countLaser > 0 )
					{
						countLaser--;
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
