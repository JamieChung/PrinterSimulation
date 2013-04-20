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
public class JobStateMap
{
	private HashMap<JobState, ArrayList<Job>> jobs;
	private ArrayList<Job> list;
	
	public JobStateMap ()
	{
		jobs = new HashMap<JobState, ArrayList<Job>>();
		
		
		for ( JobState state : JobState.values() )
		{
			jobs.put(state, new ArrayList<Job>());
		}
	}
	
	public void insert (Job j )
	{
		list = jobs.get(j.getJobState());
		list.add(j);
		
		// Sort in order of arrival times
		Collections.sort(list);
		
		jobs.put(j.getJobState(), list);

//		System.out.println("["+ Simulator.clock +"] Inserted job " + j);
//		System.out.println("["+ Simulator.clock +"] All Jobs " + jobs + "\n");
	}
	
	private void remove ( Job j )
	{
		// Get current list
		list = jobs.get(j.getJobState());
		// Remove it from current list and store updated list
		list.remove(j);
		
		Collections.sort(list);
		jobs.put(j.getJobState(), list);

//		System.out.println("["+ Simulator.clock +"] Removed job " + j);
	}
	
	public void promote ( Job j )
	{	
//		System.out.println("["+ Simulator.clock +"] Promoting job " + j);
		
		switch ( j.getJobState() )
		{
			case MACINTOSH: // Step 2
			case INITIALIZED: // Step 1
				
				remove(j);
				j.promote();
				insert(j);
				
				if ( j.getJobState() == JobState.INITIALIZED )
				{
					// Insert the new job into the initialized list
					Job _j = new Job(j.getJobSource(), JobState.INITIALIZED, j.getArrivalTime());
					insert(_j);
				}

			case NEXTSTATION: // Step 3
				// There is a 10 count limit job the LASERJET queue

				remove(j);
				
				if ( jobs.get(JobState.LASERJET).size() <  10 )
				{
					j.promote();
					insert(j);
				}
				
				break;
				
			case LASERJET:
				remove(j);
				break;
				
		default:
			break;
		}
	}
	
	public Job getFirst ( JobState state )
	{
		if ( jobs.get(state).size() > 0 )
		{
			return jobs.get(state).get(0);
		}
		
		return null;
	}
	
	public Job get ( JobState state, int index )
	{
		list = jobs.get(state);
		return list.get(index);
	}
	
	public String toString ()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n----------------\n");
		for ( JobState state : JobState.values() )
		{
			if ( jobs.get(state).size() == 0 ) continue;
			
			sb.append(state + "\t");
			sb.append(jobs.get(state).toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public boolean canPromote ( JobState state, double clock )
	{
		if ( jobs.get(state).size() > 0 )
		{
			return clock >= getFirst(state).getArrivalTime();
		}
		
		return false;
	}
	
	public int totalJobs ()
	{
		int count = 0;
		for ( JobState state : JobState.values() )
		{
			count += jobs.get(state).size();
		}
		
		return count;
	}
}