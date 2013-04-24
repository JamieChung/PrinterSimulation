
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
	
	// Mean interval average value for user groups
	public final static double JOB_INTERVAL_USER_GROUP_1 = 20.0;
	public final static double JOB_INTERVAL_USER_GROUP_2 = 40.0;
	public final static double JOB_INTERVAL_USER_GROUP_3 = 12.5;
	
	// Mean execution values for processing stations
	public static double JOB_EXECUTION_MACINTOSH = 4.5;
	public static double JOB_EXECUTION_NEXTSTATION = 5.0;
	public static double JOB_EXECUTION_LASERJET = 5.8;

	
	public static double MAC_UTIL_VALUE = 0.6975;
	public static double MAC_UTIL_LOWER_VALUE = 0.677;
	public static double MAC_UTIL_UPPER_VALUE = 0.717;

	public static double NEXT_UTIL_VALUE = 0.775;
	public static double NEXT_UTIL_LOWER_VALUE = 0.755;
	public static double NEXT_UTIL_UPPER_VALUE = 0.795;

	public static double LASER_UTIL_VALUE = 0.8536;
	public static double LASER_UTIL_LOWER_VALUE = 0.833;
	public static double LASER_UTIL_UPPER_VALUE = 0.873;
}

