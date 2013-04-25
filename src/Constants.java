
/**
 * Constants which are setup in the simulation environment.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Constants {

	// Number of times to run simulation
	public final static int SIMULATION_REPLICATION = 30;
	
	// Number of jobs to warm up simulation
	public final static int NUMBER_JOBS_WARMUP = 1000;
	
	// Number of jobs to process after warm up
	public final static int NUMBER_JOBS = 10000;
	
	// Number of jobs that can be in the printer queue
	public final static int MAX_NUMBER_JOBS_PRINTER = 10;
	
	// Mean interval average value for user groups
	public final static double JOB_INTERVAL_USER_GROUP_1 = 20.0;
	public final static double JOB_INTERVAL_USER_GROUP_2 = 40.0;
	public final static double JOB_INTERVAL_USER_GROUP_3 = 12.5;
	
	// Mean execution values for processing stations
	public final static double JOB_EXECUTION_MACINTOSH = 4.5;
	public final static double JOB_EXECUTION_NEXTSTATION = 5.0;
	public final static double JOB_EXECUTION_LASERJET = 5.8;

	
	/**
	 * ANALYTIC SOLUTION VALUES
	 */
	
	// Macintosh Utilization
	public final static double MAC_UTIL_VALUE = 0.6975;
	public final static double MAC_UTIL_LOWER_VALUE = 0.677;
	public final static double MAC_UTIL_UPPER_VALUE = 0.717;

	// NeXTstation Utilization
	public final static double NEXT_UTIL_VALUE = 0.775;
	public final static double NEXT_UTIL_LOWER_VALUE = 0.755;
	public final static double NEXT_UTIL_UPPER_VALUE = 0.795;

	// LaserJet printer Utilization
	public final static double LASER_UTIL_VALUE = 0.8536;
	public final static double LASER_UTIL_LOWER_VALUE = 0.833;
	public final static double LASER_UTIL_UPPER_VALUE = 0.873;

	// Average time a job (which completes service at all three facilities)
	// spends in the whole system (W)
	public final static double AVERAGE_TIME = 63.997951;
	public final static double AVERAGE_LOWER_TIME = 58.0; 
	public final static double AVERAGE_UPPER_TIME = 70.0;
	
	// Average number of jobs in the whole system (L)
	public final static double AVERAGE_JOBS = 9.709233;
	public final static double AVERAGE_LOWER_JOBS = 8.7;
	public final static double AVERAGE_UPPER_JOBS = 10.7;
	
}

