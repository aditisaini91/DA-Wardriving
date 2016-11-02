package accessPointLocation;

/**
 * @author Arpitha
 * @since 2016-01-25
 */
public class DAWModel {
	private int scanId;
	private double currentLat;
	private double currentLng;
	private double distance;
	private String capabilities;
	private String ssid;
	private String timeStamp;
	private String username;

	public int getScanId() {
		return scanId;
	}

	public void setScanId(int scanId) {
		this.scanId = scanId;
	}

	public double getCurrentLat() {
		return currentLat;
	}

	public void setCurrentLat(double currentLat) {
		this.currentLat = currentLat;
	}

	public double getCurrentLng() {
		return currentLng;
	}

	public void setCurrentLng(double currentLng) {
		this.currentLng = currentLng;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
