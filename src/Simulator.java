
public class Simulator {
	private JobStateMap jobs;
	public static double clock = 0.0;
	
	public Simulator ()
	{
		jobs = new JobStateMap();
	}

	public void run() {
		
		// Simulate one job
		
		Job j = new Job(JobSource.PCGROUP1, JobState.INITIALIZED, clock);
		jobs.insert(j);
		
		// Update job initialized queue
		while ( clock <= 300.0 )
		{
			for ( JobState state : JobState.values() )
			{
				if ( jobs.canPromote(state, clock) )
				{
					jobs.promote(jobs.getFirst(state));
				}
			}
			
			if ( clock % 10.0 == 0 )
			{
				System.out.println("["+clock+"]");
			}
			
			clock += 1.0;
		}
		
		System.out.println(jobs);
	}
}
