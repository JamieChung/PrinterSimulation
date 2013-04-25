import java.util.Collections;
import java.util.LinkedList;

/**
 * Manages and organizes the jobs within the system
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class JobManager
{
	/**
	 * ===============================
	 * Data Structures Documentation
	 * ===============================
	 * We decided to implement an over arching manager which handles
	 * the basic creation, deletion and sorting of the jobs. This provided
	 * a layer of abstraction for the simulation to not worry about the
	 * underlying data structure. A linked list was used for its simplicity
	 * but any sortable data structure would suffice. Since the average
	 * number of jobs was around 10 items at any given time in the system as 
	 * provided in the analytical solution, the data structure chosen is negligible
	 * because the sorting time would be essentially so small it would be constant.
	 * The simulation is able to be replicated 30 times with 11,000 jobs and
	 * provide analytical results within two seconds.
	 */
	
	// LinkedList data structure to hold all the jobs
	private LinkedList<Job> jobs = new LinkedList<Job>();
	
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
