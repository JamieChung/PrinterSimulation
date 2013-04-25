import java.util.Collections;
import java.util.LinkedList;


public class JobManager
{
	public LinkedList<Job> jobs = new LinkedList<Job>();
	
	public boolean insert ( Job j )
	{
		boolean r = jobs.add(j);
		return r;
	}
	
	public int size ()
	{
		return jobs.size();
	}
	
	public void clear ()
	{
		jobs.clear();
	}
	
	public Job getFirstJob ()
	{
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
	
	
	public boolean remove ( Job j )
	{
		return jobs.remove(j);
	}
}
