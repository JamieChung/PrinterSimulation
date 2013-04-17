
public class NumberGenerator {

	// Initial Seed Value
	public static long gv_lRandomNumberSeed = Long.parseLong("319412203455463900");

	
	static double randomNumberGenerator ()
	{
		return NumberGenerator.randomNumberGenerator(NumberGenerator.gv_lRandomNumberSeed);
	}
	
	static double randomNumberGenerator ( long plSeed )
	{
		double dZ;
		double dQuot;
		long lQuot;

		dZ = ( plSeed ) * 16807;
		dQuot = dZ / 2147483647;
		lQuot = ( long ) Math.floor( dQuot );
		dZ -= lQuot * 2147483647;
		
		// We need to update the seed value
		NumberGenerator.gv_lRandomNumberSeed = ( long ) Math.floor( dZ );
		
		return ( dZ / 2147483647 );
	}
	
	static double exponentialRVG( double dMean )
	{
		return ( -dMean * Math.log( randomNumberGenerator() ) );
	}	
}
