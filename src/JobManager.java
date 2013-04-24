import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Used to manage the jobs in the various job queues.
 * Manages the CRUD (Create Read Update Delete) operations for Jobs.te
 * 
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class JobManager
{
	// Manages the jobs based on their state
	private HashMap<JobState, ArrayList<Job>> jobs = new HashMap<JobState, ArrayList<Job>>();
	
	// Temporary list to use for job manipulation between statuses
	private ArrayList<Job> list;
	
	/**
	 * Constructor
	 */
	public JobManager ()
	{
		// For each of the job states, initialize their respective queues.
		for ( JobState state : JobState.values() )
		{
			jobs.put(state, new ArrayList<Job>());
		}
	}
	
	/**
	 * Insert a new job into the system
	 * @param j Job object
	 */
	public void insert (Job j )
	{
		// Grab the ArrayList based on the job
		list = jobs.get(j.getJobState());
		
		// Add the job to that ArrayList
		list.add(j);

		// Sort in order of arrival times
		Collections.sort(list);
		
//		if ( j.getJobState() == JobState.INITIALIZED )
//		{
//			System.out.println("Job ID: "+j.getId());
//			System.out.println(this.toString());
//		}
		
		// Replace the array list with the manipulated object
		jobs.put(j.getJobState(), list);
	}
	
	/**
	 * Remove a job from the system
	 * @param j Job object
	 */
	private void remove ( Job j )
	{
		// Get current list
		list = jobs.get(j.getJobState());
		
		// Remove it from current list and store updated list
		list.remove(j);

		// Sort in order of arrival times
		Collections.sort(list);
		
		// Replace the array list with the manipulated object
		jobs.put(j.getJobState(), list);
	}
	
	/**
	 * Attempts to promote a job within the simulation system
	 * @param j Job object
	 */
	public void promote ( Job j )
	{	
		Simulator.clock = j.getArrivalTime();
//		System.out.println("Advanced Clock: "+Simulator.clock + " -- " + j);
		// Promote based on current job state
		switch ( j.getJobState() )
		{
			case INITIALIZED: // Step 1
				
				if ( j.getJobState() == JobState.INITIALIZED )
				{
					// Insert the new job into the initialized list
					Job _j = new Job(j.getJobSource(), JobState.INITIALIZED, j.getArrivalTime());
					insert(_j);
				}
				
				remove(j);
				j.promote();
				insert(j);
				

			case MACINTOSH: // Step 2
				
				break;
				
			case NEXTSTATION: // Step 3
				// There is a 10 count limit job the LASERJET queue

//				remove(j);
				
//				if ( jobs.get(JobState.LASERJET).size() <  10 )
//				{
//					j.promote();
//					insert(j);
//				}
//				
				break;
				
			case LASERJET:
				remove(j);
				break;
				
		default:
			break;
		}
	}
	
	/**
	 * Gets the first job from a specific state queue.
	 * @param state The queue list to get the first job based on the state of the job
	 * @return First available job in a respective queue list, null if none is available
	 */
	public Job getFirst ( JobState state )
	{
		// Get the job at index = 0
		return get(state, 0);
	}
	
	/**
	 * Gets a specific job from a specific state queue
	 * @param state The queue list to search for job based on state of the job
	 * @param index Index position of the job if known before hand.
	 * @return Job at specified index, null if it is not available.
	 */
	public Job get ( JobState state, int index )
	{
		// Get the proper list queue based on the job state
		list = jobs.get(state);

		// We have to ensure that a job does exist
		if ( list.size() > 0 )
		{
			return list.get(index);
		}
		
		return null;
	}
	
	/**
	 * Checks if we can promote a job to the next stage based on the system clock.
	 * @param state Job state queue to check.
	 * @param clock System clock value.
	 * @return True if there exists at least one job which is available to promotion.
	 */
	public boolean canPromote ( JobState state, double clock )
	{
		if ( jobs.get(state).size() > 0 )
		{
			return clock >= getFirst(state).getArrivalTime();
		}
		
		return false;
	}
	
	/**
	 * Counts all the jobs within the job manager.
	 * @return Number of jobs within the job manager.
	 */
	public int totalJobs ()
	{
		int count = 0;
		
		// Loop through all the job states
		for ( JobState state : JobState.values() )
		{
			count += jobs.get(state).size();
		}
		
		return count;
	}
	
	
	
	/**
	 * Gets the total number of jobs based on a job state
	 * @param state JobState
	 * @return Number of jobs in a specific job state
	 */
	public int totalJobs ( JobState state )
	{
		return jobs.get(state).size();
	}

	
	/**
	 * Lists the job states and all the jobs within each of those state queues.
	 * @return String representation of the Job Manager
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------\n");
		
		for ( JobState state : JobState.values() )
		{
			// If the size is zero, don't bother printing the state
			if ( jobs.get(state).size() == 0 ) continue;
			
			sb.append(state + "\t");
			sb.append(jobs.get(state).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}