package accessPointLocation;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import dawConstants.Constants;
import sqlDatabase.DBConnection;

/**
 * DAWAPLocation program fetches the Scan Details from the Database, calculates
 * the exact location of Access Point and stores it in the Database.
 *
 * @author Arpitha
 * @since 2016-01-15
 */
public class DAWAPLocation {

	private DBConnection connection = new DBConnection();

	public static void main(String[] args) {
		DAWAPLocation apLocation = new DAWAPLocation();
		apLocation.MacDetails();
	}

	/**
	 * This method parses through the list of unique MAC to fetch their exact
	 * location.
	 */
	private void MacDetails() {
		List<String> uniqueMac = connection.fetchUniqueMac();
		for (int i = 0; i < uniqueMac.size(); i++) {
			Map<String, List<DAWModel>> macDetails = connection.fetchMacDetails(uniqueMac.get(i));
			for (Map.Entry<String, List<DAWModel>> entry : macDetails.entrySet()) {
				locateAccessPoint(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * This method executes the algorithm to find the location of access point
	 * and inserts the location to database
	 * 
	 * @param mac
	 *            unique MAC address
	 * @param modelList
	 *            scanned details of that particular MAC address.
	 */
	private void locateAccessPoint(String mac, List<DAWModel> modelList) {
		double[][] currentLoc = new double[modelList.size()][2];
		double[] distances = new double[modelList.size()];

		for (int i = 0; i < modelList.size(); i++) {
			double currentLat = modelList.get(i).getCurrentLat();
			double currentLng = modelList.get(i).getCurrentLng();
			currentLoc[i][0] = Constants.EARTH_RADIUS * Math.cos((currentLat) * Math.PI / 180)
					* Math.cos((currentLng) * Math.PI / 180);
			currentLoc[i][1] = Constants.EARTH_RADIUS * Math.cos((currentLat) * Math.PI / 180)
					* Math.sin((currentLng) * Math.PI / 180);
			distances[i] = modelList.get(i).getDistance();
		}

		// Need minimum of 3 scans to locate the Access Point.
		if (currentLoc.length > 2) {
			// Distance should always be positive.
			for (int i = 0; i < distances.length; i++) {
				distances[i] = Math.max(distances[i], Constants.EPSILON);
			}
			CentroidFunction centroid = new CentroidFunction(new JacobianFunction(currentLoc, distances),
					new LevenbergMarquardtOptimizer());

			Optimum optimumCentroid = centroid.getOptimumCentroidPoint();
			double[] centroidArray = optimumCentroid.getPoint().toArray();
			double zAxis = Constants.EARTH_RADIUS * Math.sin((modelList.get(0).getCurrentLat()) * Math.PI / 180);
			double radius = Math.sqrt(
					(centroidArray[0] * centroidArray[0]) + (centroidArray[1] * centroidArray[1]) + (zAxis * zAxis));
			double accessPointLat = (Math.asin(zAxis / radius)) * 180 / Math.PI;
			double accessPointLng = (Math.atan2(centroidArray[1], centroidArray[0])) * 180 / Math.PI;

			connection.insertLocation(mac, modelList.get(0).getSsid(), accessPointLat, accessPointLng,
					modelList.get(0).getCapabilities(), modelList.get(0).getTimeStamp(),
					modelList.get(0).getUsername());

		}
	}

}
