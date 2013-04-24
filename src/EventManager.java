import java.util.Collections;
import java.util.LinkedList;


public class EventManager {
	public LinkedList<Job> events = new LinkedList<Job>();
	
	public boolean insert ( Job j )
	{
		boolean r = events.add(j);
		return r;
	}
	
	public Job getFirst ()
	{
		return events.getFirst();
	}
	
	public Job getFirstJob ()
	{
		Collections.sort(events);
		Job j;		
		for ( int i = 0; i < events.size(); i++ )
		{
			j = events.get(i);
			
			// Skip completed jobs
			if ( j.getJobState() == JobState.COMPLETED ) continue;
			
			return j;
		}
		
		return null;
	}
	
	public Job getFirst (JobState state)
	{
		for ( int i = 0; i < events.size(); i++ )
		{
			if ( events.get(0).getJobState() == state )
			{
				return events.get(0);
			}
		}
		
		return null;
	}
	
	public boolean remove ( Job j )
	{
		return events.remove(j);
	}
}
