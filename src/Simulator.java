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
	 * Begins the simulation.
	 */
	public void run()
	{
		/**
		 * ===============================
		 * Initializations Documentation
		 * ===============================
		 * This is the first real method of the simulation that is executed.
		 * Here we handle the most high level parts of the simulation which is
		 * to ensure that the number of simulation replications needed are executed.
		 * Also during each replication, we ensure that the seed value will
		 * also be a random number as a starting point.
		 * In this stage it is where the warmup jobs are executed but the report
		 * is discarded and then the reports for the actual simulation in steady state
		 * is recored for later aggregation and printing.
		 * Not seen here is the initialization of the JobManager which serves
		 * as a data layer for managing, inserting, and sorting of job (events)
		 * and also a SimulationReport which handles the metrics that are measured
		 * within each simulation for computation.
		 */
		
		// Replicate the simulation a set number of times
		for ( int i = 0; i < Constants.SIMULATION_REPLICATION; i++ )
		{
			// For each simulation replication, we will use a new random seed
			NumberGenerator.gv_lRandomNumberSeed = NumberGenerator.r.nextLong();
			
			// Run the warmup jobs
			run(Constants.NUMBER_JOBS_WARMUP);
			
			// Run the steady state jobs, but this time we will store
			// the simulation report for evaluation later on
			reports.add(run(Constants.NUMBER_JOBS));	
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
		// Number of jobs successfully exited the system
		int numberCompletedJobs = 0;
		
		// Number of jobs currently in the printer queue
		int numberPrinterJobs = 0;
		
		/**
		 * ===============================
		 * Each Event Documentation
		 * ===============================
		 * Using a priority list of events, we are able to remove objects
		 * from the list, update with a new projected execution time for a future
		 * event and insert back into the list. By sorting the list, we maintain
		 * the sequential accuracy of events and can manage the edge cases of
		 * scheduling events with queues.
		 * We continue to execute each event under the number of completed
		 * jobs in the simulation is the same number as the number of jobs required
		 * for the simulation to run. This ensures that jobs that are created
		 * at the beginning are fully executed within the system and that there
		 * are no remaining jobs left idle.
		 */
		
		while ( numberCompletedJobs <= numberJobs )
		{

			/**
			 * ===============================
			 * Clock Update Documentation
			 * ===============================
			 * Since we are using a sorted list of job events, it is easy to
			 * manage the simulation clock. By sorting all the events in order
			 * of their projected execution time in ascending order, we are
			 * able to advance the simulation clock by dynamic times
			 * and not by a fixed time increment. This also improves
			 * the efficiency and accuracy of the simulation since we are
			 * not incrementing by a significant time factor which may be
			 * less accurate than the actual times generated.
			 */

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
		 * ===============================
		 * Ouput Documentation
		 * ===============================
		 * All output is done to the system console for the purpose of speed.
		 * It is important to understand the use of the SimulationReport class as an
		 * easy way to aggregate the reports and compute their averages. This was done
		 * as an approach to store and capture the metrics used in the simulation.
		 */
		
		/**
		 * Used to help compute all the values for the simulation report
		 * for all of the replications ran within this iteration
		 */
		double averageMacUtil = 0.0, averageNextUtil = 0.0, averageLaserUtil = 0.0;
		double averageTime = 0.0, averageNumberJobs = 0.0;
		
		int run = 1;
		System.out.println("\t\tpMac\t\t\t\tpNeXT\t\t\t\tpLaserJet\t\t\tW. (Average Time)\t\tL. (Average Jobs)");
		// Loop through all of the reports gathered to properly print the table
		for ( SimulationReport report : reports )
		{
			System.out.println("Run " + run + "\t\t" +
					report.macUtil() + "\t\t" + report.nextUtil() + "\t\t" + report.laserUitl() + "\t\t" +
					report.averageTime() + "\t\t" + report.averageNumberJobs());
			averageMacUtil += report.macUtil();
			averageNextUtil += report.nextUtil();
			averageLaserUtil += report.laserUitl();
			averageTime += report.averageTime();
			averageNumberJobs += report.averageNumberJobs();
			run++;
		}
		
		// Based on the size of the reports, we can get a base average
		averageMacUtil = (averageMacUtil / reports.size());
		averageNextUtil = (averageNextUtil / reports.size());
		averageLaserUtil = (averageLaserUtil / reports.size());
		averageTime = (averageTime / reports.size());
		averageNumberJobs = (averageNumberJobs / reports.size());
		
		// Line break
		System.out.println("\n------------\n");
		
		System.out.println("Average:\t" + averageMacUtil + "\t\t" + averageNextUtil + "\t\t" + averageLaserUtil +"\t\t" + 
				averageTime + "\t\t" + averageNumberJobs);

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
	
}
