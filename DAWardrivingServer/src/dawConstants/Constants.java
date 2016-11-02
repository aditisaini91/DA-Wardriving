package dawConstants;

/**
 * List of all the Constant values used in the program.
 *
 * @author Arpitha
 * @since 2016-01-25
 */
public class Constants {
	public static final double EPSILON = 1E-7;
	public static final int EARTH_RADIUS = 6371000; //in metres
	public static final int MAX_ITERATIONS = 1000;
	private static final int DB_PORT = 3306;
	private static final String DB_HOST = "127.0.0.1";
	private static final String DATABASE_NAME = "dawardriving";
	public static final String CONNECTION_URL = "jdbc:mysql://" + Constants.DB_HOST + ":" + Constants.DB_PORT + "/" + Constants.DATABASE_NAME;
	public static final String DB_USERNAME = "root";
	public static final String DB_PASSWORD = "root";
	public static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
}
