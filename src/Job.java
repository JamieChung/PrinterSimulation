
/**
 * Job entity which is managed in various state queues.
 * Implements Comparable class so that it can be sorted in list.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Job implements Comparable<Object> {
	
	// Mean Arrival Time based on the job source
	private double meanArrivalTime;
	
	// Arrival time for current state in the system
	private double arrivalTime;
	
	// Source of the job 
	private JobSource source;
	
	// Current state queue where the job is in the system
	private JobState state;
	
	// Unique ID for a job in the system
	public static int id = 0;
	
	/**
	 * Constructor to setup new job
	 * @param _source Source of the job to enter the system
	 * @param _state Initial state of the job
	 * @param currentTime Current time of the system clock
	 */
	public Job ( JobSource _source, JobState _state, double currentTime )
	{
		source = _source;
		state = _state;
		
		// Increment by one
		id = Job.id + 1;
		
		// Set the mean arrival time based on the job source
		switch ( source )
		{
			case PCGROUP1:
				meanArrivalTime = Constants.JOB_INTERVAL_USER_GROUP_1;
				break;
				
			case PCGROUP2:
				meanArrivalTime = Constants.JOB_INTERVAL_USER_GROUP_2;
				break;
				
			case PCGROUP3:
				meanArrivalTime = Constants.JOB_INTERVAL_USER_GROUP_3;
				break;
		}

		arrivalTime = NumberGenerator.exponentialRVG(meanArrivalTime) + currentTime;
	}
	
	
	/**
	 * Promote the job state of a job
	 * Also handles the updating of the new arrival time based on the execution time
	 * of previous states in the system.
	 */
	public void promote ()
	{
		switch ( state )
		{
			case INITIALIZED:
				state = JobState.MACINTOSH;
				arrivalTime += NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_MACINTOSH);
				break;
				
			case MACINTOSH:
				state = JobState.NEXTSTATION;
				arrivalTime += NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION);
				break;
				
			case NEXTSTATION:
				state = JobState.LASERJET;
				arrivalTime += NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_LASERJET);
				break;
				
			case LASERJET:
				break;
		}
		
	}
	
	/**
	 * Gets the current job state
	 * @return JobState
	 */
	public JobState getJobState ()
	{
		return state;
	}
	
	/**
	 * Gets the arrival time of the job in the current state in system
	 * @return Unit time
	 */
	public double getArrivalTime ()
	{	
		return arrivalTime;
	}
	
	/**
	 * Gets the source of the current job
	 * @return JobSource
	 */
	public JobSource getJobSource ()
	{
		return source;
	}
	
	/**
	 * Easy string representation of a job object for debugging
	 * @return <Job.source> [<Job.state>] - ArrivalTime: <Job.arrivalTime>
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(source);
		sb.append(" [" + state + "] ");
		sb.append(" - Arrival Time: " + arrivalTime);
		return sb.toString();
	}

	/**
	 * Used to compare two job objects when sorting in a list.
	 * Objects are sorted in order of arrival times.
	 */
	public int compareTo(Object arg) {
		
		Job j;
		if ( arg instanceof Job )
		{
			j = (Job) arg;
			
			if ( arrivalTime < j.getArrivalTime() )
			{
				return -1;
			}
			else if ( arrivalTime == j.getArrivalTime() )
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
		
		return 1;
	}

}
