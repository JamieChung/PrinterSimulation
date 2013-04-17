import java.util.ArrayList;
import java.util.HashMap;

public class Simulator {

	private HashMap<JobState, ArrayList<Job>> jobs;
	private double clock = 0.0;
	
	public Simulator ()
	{
		jobs = new HashMap<JobState, ArrayList<Job>>();

	}

	public void run() {
		// TODO Auto-generated method stub

		System.out.println(NumberGenerator.randomNumberGenerator());
		System.out.println(NumberGenerator.randomNumberGenerator());
		System.out.println(NumberGenerator.randomNumberGenerator());
		System.out.println(NumberGenerator.randomNumberGenerator());
	}
}
