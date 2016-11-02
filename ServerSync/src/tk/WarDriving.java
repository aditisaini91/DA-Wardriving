package tk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author Preethini and Aditi
 * @since 2016-01-15
 */
public class WarDriving {

	private Connection connection = null;

	public static void main(String[] args) {
		WarDriving warDriving = new WarDriving();
		List<WifiData> wifiPointList = warDriving.fetchWifiPoints();
		System.out.println("Size of array: " + wifiPointList.size());
		warDriving.sendData(wifiPointList);
	}

	/**
	 * This method establishes the connection with the server.
	 */
	private void dbConnSetup() {
		String dbHost = "127.0.0.1";
		int dbPort = 3306;
		String databaseName = "dawardriving";
		String connectionUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + databaseName;
		String dbUsername = "root";
		String dbPassword = "root";
		String driverName = "com.mysql.jdbc.Driver";

		try {
			Class.forName(driverName).newInstance();
			connection = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method closes the existing connection.
	 */
	private void dbConnClose() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Successfully closed DB Connection");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method retrieves data from DB (AccessPointDetails) and returns list
	 * of WifiData objects
	 */
	private List<WifiData> fetchWifiPoints() {
		List<WifiData> wifiList = new ArrayList<>();
		String query = "SELECT * FROM AccessPointDetails;";
		dbConnSetup();
		try {
			if (connection != null) {
				System.out.println("Connection established");

				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					WifiData data = new WifiData();
					data.setBSSID(rs.getString("BSSID"));
					data.setSSID(rs.getString("SSID"));
					data.setCapabilities(rs.getString("capabilities"));
					data.setLatitude(rs.getString("latitude"));
					data.setLongitude(rs.getString("longitude"));
					data.setTimestamp(rs.getString("timestamp"));
					data.setUsername(rs.getString("username"));
					wifiList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbConnClose();
		return wifiList;
	}

	/**
	 * This method sends data retrieved from DB, to server.
	 */
	@SuppressWarnings("unchecked")
	public void sendData(List<WifiData> wifiList) {
		try {
			URL url = new URL("http://130.83.163.64/api");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			JSONObject request = new JSONObject();
			request.put("api_key", "e23a470b67a94a13af3b1f981d828112");

			JSONArray wifiInfo = new JSONArray();
			// iterating through the list and converting to json format
			for (final WifiData data : wifiList) {
				JSONObject wifiObj = new JSONObject();

				String str2 = "WPA";
				Boolean is_open = true;

				// check for open/password-protected network
				if (data.getCapabilities().toLowerCase().contains(str2.toLowerCase())) {
					is_open = false;
				}
				wifiObj.put("ssid", data.getSSID());
				wifiObj.put("bssid", data.getBSSID());
				wifiObj.put("long", data.getLongitude());
				wifiObj.put("lat", data.getLatitude());
				wifiObj.put("added_by", data.getUsername());
				wifiObj.put("is_open", is_open);
				wifiObj.put("encryption_type", data.getCapabilities());
				wifiObj.put("date_found", data.getTimestamp());

				wifiInfo.add(wifiObj);
			}
			request.put("wifi_info", wifiInfo.toJSONString());

			OutputStream os = conn.getOutputStream();
			os.write(request.toJSONString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String line, jsonData = "";
			System.out.println("Output from Server .... \n");
			while ((line = br.readLine()) != null) {
				jsonData += line + "\n";
			}
			conn.disconnect();

			// parse the JSON response
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(jsonData);
			JSONObject array = (JSONObject) obj;
			System.out.println(array.get("friendlyMessage"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
