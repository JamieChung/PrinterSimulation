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
	
	private JobManager jobs = new JobManager();
	public ArrayList<SimulationReport> reports = new ArrayList<SimulationReport>();

	public String checkBounds ( double x, double lower, double upper )
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(" - ");
		sb.append("[" + lower + ", " + upper + "] ");
		if ( x < lower || x > upper )
		{
			sb.append("OUT OF BOUNDS");
		}
		
		
		return sb.toString();
	}
	
	/**
	 * Begins the simulation.
	 */
	public void run()
	{
		Random r = new Random();
		
		for ( int i = 0; i < Constants.SIMULATION_REPLICATION; i++ )
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

		System.out.println("Running " + Constants.SIMULATION_REPLICATION + " Simulation Replications");
		System.out.println("Warmup Jobs: " + Constants.NUMBER_JOBS_WARMUP);
		System.out.println("Steady-state Jobs: "+ Constants.NUMBER_JOBS);
		
		// Average Macintosh Utilization
		System.out.println("Macintosh Utilization: " + averageMacUtil + 
				checkBounds(averageMacUtil, Constants.MAC_UTIL_LOWER_VALUE, Constants.MAC_UTIL_UPPER_VALUE));
		
		// Average NeXTstation Utilization
		System.out.println("NeXTstation Utilization: " + averageNextUtil + 
				checkBounds(averageNextUtil, Constants.NEXT_UTIL_LOWER_VALUE, Constants.NEXT_UTIL_UPPER_VALUE));
		
		// Average LaserJet Utilization
		System.out.println("LaserJet Utilization: " + averageLaserUtil + 
				checkBounds(averageLaserUtil, Constants.LASER_UTIL_LOWER_VALUE, Constants.LASER_UTIL_UPPER_VALUE));
		
		// Average Time Job spends in entire system
		System.out.println("Average Time (W): " + averageTime + 
				checkBounds(averageTime, Constants.AVERAGE_LOWER_TIME, Constants.AVERAGE_UPPER_TIME));
		
		// Average number of jobs in whole system
		System.out.println("Average Number Jobs (L): " + averageNumberJobs + 
				checkBounds(averageNumberJobs, Constants.AVERAGE_LOWER_JOBS, Constants.AVERAGE_UPPER_JOBS));
	}
	
	private SimulationReport run ( int number_jobs )
	{
		SimulationReport report = new SimulationReport(number_jobs);
		Job.incremental_id = 0;
		
		jobs.insert(new Job(JobSource.PCGROUP1, JobState.INITIALIZED, report.clock));
		jobs.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, report.clock));
		jobs.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, report.clock));
		
		
		Job j;
		int completeCount = 0;
		int countLaser = 0;
		while ( completeCount <= number_jobs )
		{
			// Find earliest job 
			// Also dequeues from the top of the queue
			j = jobs.getFirstJob();
			if ( j == null ) break;

			report.clock = j.getArrivalTime();
			
			
			switch ( j.getJobState() )
			{
				case INITIALIZED:
					// Promote to MAC state
					j.state = JobState.MACINTOSH;
					jobs.insert(j);
					
				break;
				
				case MACINTOSH:

					if (Job.incremental_id < number_jobs )
					{
						// Insert a new job
						jobs.insert(new Job(j.getJobSource(), JobState.INITIALIZED, report.clock));
						report.updateAverageNumberJobs(jobs.size());
					}
					
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
					jobs.insert(j);
				break;
				
				case NEXTSTATION:
					
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
					jobs.insert(j);
					
					break;
					
				case NEXTSTATION_FINISHED:
					
					jobs.remove(j);
					
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
						jobs.insert(j);
					}
					else
					{
						completeCount++;
					}
					
					break;
					
				case LASERJET:
					
					jobs.remove(j);
					
					report.jobHistory += (report.clock - j.systemStartTime);
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
}
