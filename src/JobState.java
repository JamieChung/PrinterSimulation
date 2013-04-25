
/**
 * Various states that a job can obtain.
 * Used to organize various job queues.
 * @author Jamie Chung <jfchung@vt.edu>
 *
 */
public enum JobState
{
	INITIALIZED,
	MACINTOSH,
	NEXTSTATION,
	NEXTSTATION_FINISHED,
	LASERJET,
	COMPLETED
}
