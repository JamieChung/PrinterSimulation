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
	// Manages the job events
	private JobManager jobs = new JobManager();
	
	// Holds all the reports that will be averaged for their util values
	public ArrayList<SimulationReport> reports = new ArrayList<SimulationReport>();
	
	
	/**
	 * Used to easily check parameters between a confidence interval
	 * @param x Actual value to check
	 * @param lower Lower bounds for the value
	 * @param upper Upper bounds for the value
	 * @return String output with the x value, bounds and whether it is out of bounds
	 */
	public String checkBounds ( double x, double lower, double upper )
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(" - ");
		sb.append("[" + lower + ", " + upper + "] ");
		
		// Checks the x value with the surrounding bounds
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
		SimulationReport warmup;
		SimulationReport report;
		// Replicate the simulation a set number of times
		for ( int i = 0; i < Constants.SIMULATION_REPLICATION; i++ )
		{
			// For each simulation replication, we will use a new random seed
			NumberGenerator.gv_lRandomNumberSeed = NumberGenerator.r.nextLong();
			
			// Run the warmup jobs
			warmup = run(Constants.NUMBER_JOBS_WARMUP);
			report = run(Constants.NUMBER_JOBS);
			
			// Run the steady state jobs, but this time we will store
			// the simulation report for evaluation later on
			reports.add(report);	
		}
		
		// Print the report gathered to the console
		printReport();
	}
	
	/**
	 * Runs the simulator for a set number of jobs
	 * @param numberJobs
	 * @return SimulationReport holding all the metrics gathered during simulation
	 */
	private SimulationReport run ( int numberJobs )
	{
		// Setup a new Simulation Report
		SimulationReport report = new SimulationReport(numberJobs);
		
		// Reset the global Job unique ID counter
		Job.incremental_id = 0;
		
		// Delete all existing jobs
		jobs.clear();
		
		// Insert the first three base jobs
		jobs.insert(new Job(JobSource.PCGROUP1, JobState.INITIALIZED, report.clock));
		jobs.insert(new Job(JobSource.PCGROUP2, JobState.INITIALIZED, report.clock));
		jobs.insert(new Job(JobSource.PCGROUP3, JobState.INITIALIZED, report.clock));
		
		
		Job j;
		int numberCompletedJobs = 0;
		int numberPrinterJobs = 0;
		while ( numberCompletedJobs <= numberJobs )
		{
			// Find earliest job 
			// Also dequeues from the top of the queue
			j = jobs.getFirstJob();
			if ( j == null ) break;

			// Advances the simulation clock to the earliest event
			report.clock = j.getArrivalTime();
			
			// Based on the state we execute
			switch ( j.getJobState() )
			{
				case INITIALIZED:
					// Promote to Macintosh stage and reinsert
					j.state = JobState.MACINTOSH;
					jobs.insert(j);

					// Update area counts
					report.updateAverageNumberJobs(jobs.size());
					
					// Insert a new job
					jobs.insert(new Job(j.getJobSource(), JobState.INITIALIZED, report.clock));
				break;
				
				case MACINTOSH:
					
					// Generate time for when Macintosh job will finish executing
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_MACINTOSH);
					
					// Add it to the history of total execution time for this system
					report.macHistory += j.executionTime;
					
					// If our projected arrival time is less than when the clock will be idle
					if ( (report.clock + j.executionTime) < report.macClock )
					{
						// New arrival time will be the execution time after when the clock is idle
						j.arrivalTime = report.macClock + j.executionTime;
					}
					else
					{
						// Idle
						j.arrivalTime += j.executionTime;
					}
					
					report.macClock = j.arrivalTime;
					j.state = JobState.NEXTSTATION;
					jobs.insert(j);
				break;
				
				case NEXTSTATION:
					
					// Generate time for when NeXTstation job will finish executing
					j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION);
					
					// Add it to the history of the total execution time for this system
					report.nextHistory += j.executionTime;

					// If our projected arrival time is less than when the clock will be idle
					if ( (report.clock + j.executionTime) < report.nextClock )
					{
						// New arrival time will be the execution time after when the clock is idle
						j.arrivalTime = report.nextClock + j.executionTime;
					}
					else
					{
						// Idle
						j.arrivalTime += j.executionTime;
					}
					
					report.nextClock = j.arrivalTime;
					j.state = JobState.NEXTSTATION_FINISHED;
					jobs.insert(j);
					break;
					
				case NEXTSTATION_FINISHED:
					
					// Respect the max number of jobs the printer can handle at any time
					if ( numberPrinterJobs < Constants.MAX_NUMBER_JOBS_PRINTER )
					{
						numberPrinterJobs++;
						
						// Generate time for when LaserJet job will finish executing
						j.executionTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_LASERJET);

						// Add it to the history of the total execution time for this system
						report.laserHistory += j.executionTime;
						
						if ( (report.clock + j.executionTime) < report.laserClock )
						{
							// New arrival time will be the execution time after when the clock is idle
							j.arrivalTime = report.laserClock + j.executionTime;
						}
						else
						{
							// Idle
							j.arrivalTime += j.executionTime;
						}
						
						report.laserClock = j.arrivalTime;
						j.state = JobState.LASERJET;
						jobs.insert(j);
					}
					else
					{
						// Safety exit the system

						// Number of jobs that have exited the system
						numberCompletedJobs++;
					}
					
					break;
					
				case LASERJET:
					
					// Number of jobs that have exited the system
					numberCompletedJobs++;
					
					// Report the life span of the job that has successfully been processed
					report.jobHistory += (report.clock - j.systemStartTime);
					
					// Safety net so we do not gain negative numbers
					if ( numberPrinterJobs > 0 )
					{
						numberPrinterJobs--;
					}
					
					break;
			default:
				break;
			}
		}
		
		// Send back the report to be evaluated
		return report;
	}
	
	
	/**
	 * Computes all the values from the simulation reports
	 * and prints them to the console.
	 */
	public void printReport ()
	{

		/**
		 * Used to help compute all the values for the simulation report
		 * for all of the replications ran within this iteration
		 */
		double averageMacUtil = 0.0, averageNextUtil = 0.0, averageLaserUtil = 0.0;
		double averageTime = 0.0, averageNumberJobs = 0.0;
		
		// Loop through all of the reports gathered
		for ( SimulationReport report : reports )
		{
			averageMacUtil += report.macUtil();
			averageNextUtil += report.nextUtil();
			averageLaserUtil += report.laserUitl();
			averageTime += report.averageTime();
			averageNumberJobs += report.averageNumberJobs();
		}
		
		// Based on the size of the reports, we can get a base average
		averageMacUtil = (averageMacUtil / reports.size());
		averageNextUtil = (averageNextUtil / reports.size());
		averageLaserUtil = (averageLaserUtil / reports.size());
		averageTime = (averageTime / reports.size());
		averageNumberJobs = (averageNumberJobs / reports.size());

		// Output initial configuration for this simulation run
		System.out.println("Running " + Constants.SIMULATION_REPLICATION + " Simulation Replications");
		System.out.println("Warmup Jobs: " + Constants.NUMBER_JOBS_WARMUP);
		System.out.println("Steady-state Jobs: "+ Constants.NUMBER_JOBS);
		
		// Line break
		System.out.println("\n------------\n");
		
		// Average Macintosh Utilization
		System.out.println("Average Macintosh Utilization: " + averageMacUtil + 
				checkBounds(averageMacUtil, Constants.MAC_UTIL_LOWER_VALUE, Constants.MAC_UTIL_UPPER_VALUE));
		
		// Average NeXTstation Utilization
		System.out.println("Average NeXTstation Utilization: " + averageNextUtil + 
				checkBounds(averageNextUtil, Constants.NEXT_UTIL_LOWER_VALUE, Constants.NEXT_UTIL_UPPER_VALUE));
		
		// Average LaserJet Utilization
		System.out.println("Average LaserJet Utilization: " + averageLaserUtil + 
				checkBounds(averageLaserUtil, Constants.LASER_UTIL_LOWER_VALUE, Constants.LASER_UTIL_UPPER_VALUE));
		
		// Average Time Job spends in entire system
		System.out.println("Average Time (W): " + averageTime + 
				checkBounds(averageTime, Constants.AVERAGE_LOWER_TIME, Constants.AVERAGE_UPPER_TIME));
		
		// Average number of jobs in whole system
		System.out.println("Average Number Jobs (L): " + averageNumberJobs + 
				checkBounds(averageNumberJobs, Constants.AVERAGE_LOWER_JOBS, Constants.AVERAGE_UPPER_JOBS));
	}
}
