
/**
 * Job entity which is managed in various state queues.
 * Implements Comparable class so that it can be sorted in list.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public class Job implements Comparable<Object> {
	
	private double meanArrivalTime;
	private double previousArrivalTime;
	private double arrivalTime;
	private JobSource source;
	private JobState state;
	
	public Job ( JobSource _source, JobState _state, double currentTime )
	{
		source = _source;
		state = _state;
		
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
	
	public void promote ()
	{
		setPreviousArrivalTime(arrivalTime);
		
		switch ( state )
		{
			case INITIALIZED:
				state = JobState.MACINTOSH;
				arrivalTime = NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_MACINTOSH) + arrivalTime;
				break;
				
			case MACINTOSH:
				state = JobState.NEXTSTATION;
				arrivalTime+= NumberGenerator.exponentialRVG(Constants.JOB_EXECUTION_NEXTSTATION) + arrivalTime;
				break;
				
			case NEXTSTATION:
				state = JobState.LASERJET;
				break;
				
			case LASERJET:
				break;
//			case LASERJET_FINISHED:
//				break;
//			case MACINTOSH_FINISHED:
//				break;
//			case NEXTSTATION_FINISHED:
//				break;
//			default:
//				break;
		}
		
	}
	
	public JobState getJobState ()
	{
		return state;
	}
	
	public double getArrivalTime ()
	{	
		return arrivalTime;
	}
	
	public JobSource getJobSource ()
	{
		return source;
	}
	
	public String toString ()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(source);
		sb.append(" [" + state + "] ");
		sb.append(" - Arrival Time: " + arrivalTime);
		return sb.toString();
	}

	@Override
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

	public double getPreviousArrivalTime() {
		return previousArrivalTime;
	}

	public void setPreviousArrivalTime(double previousArrivalTime) {
		this.previousArrivalTime = previousArrivalTime;
	}
}
