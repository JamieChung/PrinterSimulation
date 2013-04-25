import java.util.Random;

/**
 * Number Generator for the simulation.
 * 
 * @author Jamie Chung <jfchung@vt.edu>
 * @author Dr. Osman Balci <balci@vt.edu>
 * 
 */
public class NumberGenerator
{
	// Random for generating new seed values
	public static Random r = new Random();

	// Initial Seed Value
	public static long gv_lRandomNumberSeed = r.nextLong();
	
	/**
	 * Generates a random number based on the current number seed
	 * 
	 * @return Random number
	 */
	static double randomNumberGenerator() {
		return NumberGenerator
				.randomNumberGenerator(NumberGenerator.gv_lRandomNumberSeed);
	}

	/**
	 * Generates a random number based on specified seed value
	 * 
	 * @param plSeed Seed value to generate random number from
	 * @return Random number
	 */
	
	static double randomNumberGenerator(long plSeed)
	{
		/**
		 * ===============================
		 * Random Number Generator Documentation
		 * ===============================
		 * The random number generator was provided as a part of the 
		 * analytic solution in order to replicate similar values.
		 * The use of a seed value is important because it ensures
		 * that simulations that are once run are able to be replicated.
		 * It is important to understand that the seed value changes
		 * every time this function is ran in order to appear random,
		 * but still be sequential in the numbers generated in replication.
		 */
		
		
		double dZ;
		double dQuot;
		long lQuot;

		dZ = (plSeed) * 16807;
		dQuot = dZ / 2147483647;
		lQuot = (long) Math.floor(dQuot);
		dZ -= lQuot * 2147483647;

		// We need to update the seed value
		NumberGenerator.gv_lRandomNumberSeed = (long) Math.floor(dZ);

		return (dZ / 2147483647);
	}

	/**
	 * Generates an Exponential Random Variate
	 * 
	 * @param dMean Mean value of the random variate
	 * @return Random number based on the exponential RV
	 */
	
	static double exponentialRVG(double dMean)
	{

		/**
		 * ===============================
		 * Exponential Random Variate Generator Documentation
		 * ===============================
		 * The exponential distribution was the chosen distribution family
		 * for this simulation to determine the sequence of time events.
		 * By providing the mean of the event to occur, we are able to 
		 * isolate and compute a genuinely random variate that will
		 * fit normal without any deviant outliers.
		 */
		return (-dMean * Math.log(randomNumberGenerator()));
	}
}
