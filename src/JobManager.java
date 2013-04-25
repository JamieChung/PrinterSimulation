import java.util.Collections;
import java.util.LinkedList;

/**
 * Manages and organizes the jobs within the system
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class JobManager
{
	// LinkedList data structure to hold all the jobs
	public LinkedList<Job> jobs = new LinkedList<Job>();

	/**
	 * Inserts a new job in the system
	 * @param j Job object
	 * @return True if successful
	 */
	public boolean insert ( Job j )
	{
		return jobs.add(j);
	}
		
	/**
	 * Removes a job from the system
	 * @param j Job object
	 * @return True if job was able to be removed
	 */
	public boolean remove ( Job j )
	{
		return jobs.remove(j);
	}
	
	/**
	 * Gets the first non completed job in the system and removes it
	 * This is so that if it the state is updated, it can be reinserted
	 * @return First job in the system, null if non available
	 */
	public Job getFirstJob ()
	{
		// Sort all the jobs based on the comparedTo sorting method
		Collections.sort(jobs);
		
		Job j;		
		for ( int i = 0; i < jobs.size(); i++ )
		{
			j = jobs.get(i);
			
			// Skip completed jobs
			if ( j.getJobState() == JobState.COMPLETED ) continue;
			
			remove(j);
			
			return j;
		}
		
		return null;
	}
	
	/**
	 * Returns the size of the jobs in the system
	 * @return Number of jobs in the system
	 */
	public int size ()
	{
		return jobs.size();
	}
	
	/**
	 * Clears the jobs in the system
	 */
	public void clear ()
	{
		jobs.clear();
	}
}
