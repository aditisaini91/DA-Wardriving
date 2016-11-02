package sqlDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import accessPointLocation.DAWModel;
import dawConstants.Constants;

/**
 * Establish connection to Database and query for the required information.
 *
 * @author Preethini
 * @since 2016-01-20
 */
public class DBConnection {

	private Connection connection = null;

	/**
	 * This method establishes a connection to the remote Database.
	 */
	private void dbConnSetup() {
		try {
			Class.forName(Constants.DRIVER_NAME).newInstance();
			connection = DriverManager.getConnection(Constants.CONNECTION_URL, Constants.DB_USERNAME,
					Constants.DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method closes the connection to the remote Database.
	 */
	private void dbConnClose() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method queries to fetch the unique MAC ID's from the Database.
	 * 
	 * @return List of unique MAC IDs.
	 */
	public List<String> fetchUniqueMac() {
		List<String> uniqueMacList = new ArrayList<>();
		String query = "SELECT DISTINCT BSSID FROM WifiScans;";
		dbConnSetup();
		try {
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					uniqueMacList.add(rs.getString("BSSID"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbConnClose();
		return uniqueMacList;
	}

	/**
	 * This method queries the Database to fetch the details of different scans
	 * for a particular MAC address.
	 * 
	 * @param mac
	 *            MAC address for which scan details have to be retrieved from
	 *            the Database.
	 * @return Map key is the MAC address and value is the List containing
	 *         details of that MAC address for different scans.
	 */
	public Map<String, List<DAWModel>> fetchMacDetails(String mac) {
		Map<String, List<DAWModel>> macDetails = new HashMap<>();
		String query = "SELECT scan_id, ssid, lat_current, long_current, distance, capabilities, timestamp, username FROM WifiScans WHERE BSSID = \""
				+ mac + "\" AND distance < 100 ORDER BY distance LIMIT 10;";
		dbConnSetup();
		try {
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				List<DAWModel> macDetailsList = new ArrayList<>();
				while (rs.next()) {
					DAWModel model = new DAWModel();
					model.setScanId(Integer.parseInt(rs.getString("scan_id")));
					model.setCurrentLat(Double.parseDouble(rs.getString("lat_current")));
					model.setCurrentLng(Double.parseDouble(rs.getString("long_current")));
					model.setDistance(Double.parseDouble(rs.getString("distance")));
					model.setCapabilities(rs.getString("capabilities"));
					model.setSsid(rs.getString("ssid"));
					model.setTimeStamp(rs.getString("timestamp"));
					model.setUsername(rs.getString("username"));

					macDetailsList.add(model);
				}
				macDetails.put(mac, macDetailsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbConnClose();
		return macDetails;
	}

	/**
	 * This method inserts the exact Location of the Access Point to the
	 * Database.
	 * 
	 * @param mac
	 *            MAC for which the exact Location is determined.
	 * @param latitude
	 *            Latitude of a particular MAC.
	 * @param longitude
	 *            Longitude of a particular MAC.
	 * @param capabilities
	 *            Capabilities of the Access Points.
	 */
	public void insertLocation(String mac, String ssid, double latitude, double longitude, String capabilities, String timestamp, String username) {
		dbConnSetup();
		try {
			if (connection != null) {
				String query = "INSERT INTO AccessPointDetails (BSSID, latitude, longitude, capabilities, SSID, timestamp, username)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, mac);
				preparedStmt.setDouble(2, latitude);
				preparedStmt.setDouble(3, longitude);
				preparedStmt.setString(4, capabilities);
				preparedStmt.setString(5, ssid);
				preparedStmt.setString(6, timestamp);
				preparedStmt.setString(7, username);

				preparedStmt.execute();
			}
		} catch (Exception e) {
			String query = "UPDATE AccessPointDetails SET latitude = ?, longitude = ?, capabilities = ?, SSID = ?, timestamp = ?, username = ? WHERE BSSID = ?";
			try {
				if (connection != null) {
					PreparedStatement preparedStmt = connection.prepareStatement(query);
					preparedStmt.setDouble(1, latitude);
					preparedStmt.setDouble(2, longitude);
					preparedStmt.setString(3, capabilities);
					preparedStmt.setString(4, ssid);
					preparedStmt.setString(5, mac);
					preparedStmt.setString(6, timestamp);
					preparedStmt.setString(7, username);
					
					preparedStmt.executeUpdate();
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		dbConnClose();
	}

}
