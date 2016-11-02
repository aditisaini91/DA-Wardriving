package accessPointLocation;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;

import dawConstants.Constants;

/**
 * CentroidFunction calculates calculates the Location of the access point.
 *
 * @author Arpitha
 * @since 2016-01-25
 */
public class CentroidFunction {
	private JacobianFunction jacobianFunction;
	private LeastSquaresOptimizer leastSquaresOptimizer;

	public CentroidFunction(JacobianFunction jacobianFunction, LeastSquaresOptimizer leastSquaresOptimizer) {
		this.jacobianFunction = jacobianFunction;
		this.leastSquaresOptimizer = leastSquaresOptimizer;
	}

	/**
	 * This method calculates the centroid point of the jacobian
	 * matrix(containing the user's location) and executes the least Squares
	 * optimizer to fetch the location of the access points.
	 */
	public Optimum getOptimumCentroidPoint() {
		int locationCount = jacobianFunction.getCurrentLoc().length;
		int dimension = jacobianFunction.getCurrentLoc()[0].length;
		double[] centroidPoint = new double[dimension];
		double[] observedArray = new double[locationCount];
		double[] weights = new double[observedArray.length];
		double[] distances = jacobianFunction.getDistances();

		for (int i = 0; i < observedArray.length; i++) {
			weights[i] = distances[i] * distances[i];
		}

		// centroidPoint = (x0+x1+....+xn, y0+y1+....+yn)
		for (int i = 0; i < locationCount; i++) {
			double[] vertex = jacobianFunction.getCurrentLoc()[i];
			for (int j = 0; j < vertex.length; j++) {
				centroidPoint[j] += vertex[j];
			}
		}

		// centroidPoint = ((x0+x1+....+xn)/n, (y0+y1+....+yn)/n)
		for (int i = 0; i < centroidPoint.length; i++) {
			centroidPoint[i] /= locationCount;
		}

		LeastSquaresProblem leastSquaresProblem = LeastSquaresFactory.create(jacobianFunction,
				new ArrayRealVector(observedArray, false), new ArrayRealVector(centroidPoint, false),
				new DiagonalMatrix(distances), null, Constants.MAX_ITERATIONS, Constants.MAX_ITERATIONS);

		return leastSquaresOptimizer.optimize(leastSquaresProblem);
	}
}
