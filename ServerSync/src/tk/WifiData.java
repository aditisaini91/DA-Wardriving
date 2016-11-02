package tk;

/**
 * @author Preethini and Aditi
 * @since 2016-02-01
 */
public class WifiData {
	private String BSSID;
	private String SSID;
	private String capabilities;
	private String latitude;
	private String longitude;
	private String timestamp;
	private String username;
	
	public String getBSSID() {
		return BSSID;
	}
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
