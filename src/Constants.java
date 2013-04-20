
/**
 * Constants which are setup in the simulation environment.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Constants {

	// Number of times to run simulation
	public static int SIMULATION_REPLICATION = 30;
	
	// Number of jobs to warm up simulation
	public static int NUMBER_JOBS_WARMUP = 1000;
	
	// Number of jobs to process after warm up
	public static int NUMBER_JOBS = 10000;
	
	// Mean interval average value for user groups
	public static double JOB_INTERVAL_USER_GROUP_1 = 20.0;
	public static double JOB_INTERVAL_USER_GROUP_2 = 40.0;
	public static double JOB_INTERVAL_USER_GROUP_3 = 12.5;
	
	// Mean execution values for processing stations
	public static double JOB_EXECUTION_MACINTOSH = 4.5;
	public static double JOB_EXECUTION_NEXTSTATION = 5;
	public static double JOB_EXECUTION_LASERJET = 5.8;
}

