import java.util.ArrayList;
import java.util.HashMap;

public class Simulator {

	private HashMap<JobState, ArrayList<Job>> jobs;
	private double clock = 0.0;
	
	public Simulator ()
	{
		jobs = new HashMap<JobState, ArrayList<Job>>();
		
		for ( JobState state : JobState.values() )
		{
			jobs.put(state, new ArrayList<Job>());
		}
	}

	public void run() {
		
		while ( clock <= 1000.0 )
		{
			
			
			
			
			clock++;
		}
	}
}
