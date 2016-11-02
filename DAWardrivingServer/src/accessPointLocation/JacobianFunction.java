package accessPointLocation;

import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/**
 * @author Arun Kumar
 * @since 2016-01-25
 */
public class JacobianFunction implements MultivariateJacobianFunction {

	private double currentLoc[][];
	private double distances[];

	public JacobianFunction(double currentLoc[][], double distances[]) {
		this.currentLoc = currentLoc;
		this.distances = distances;
	}

	public double[][] getCurrentLoc() {
		return currentLoc;
	}

	public double[] getDistances() {
		return distances;
	}

	/**
	 * @param point
	 *            for which slope has to be calculated.
	 * @return Jacobian matrix of the point.
	 */
	private RealMatrix jacobianMatrix(RealVector point) {
		double[] pointToArray = point.toArray();
		double[][] jacobianArray = new double[currentLoc.length][pointToArray.length];

		for (int i = 0; i < jacobianArray.length; i++) {
			for (int j = 0; j < pointToArray.length; j++) {
				jacobianArray[i][j] = 2 * pointToArray[j] - 2 * currentLoc[i][j];
			}
		}
		return new Array2DRowRealMatrix(jacobianArray);
	}

	@Override
	public Pair<RealVector, RealMatrix> value(RealVector point) {
		double[] pointToArray = point.toArray();
		double[] leastSquareArray = new double[distances.length];

		for (int i = 0; i < leastSquareArray.length; i++) {
			leastSquareArray[i] = 0.0;
			for (int j = 0; j < pointToArray.length; j++) {
				leastSquareArray[i] += (pointToArray[j] - getCurrentLoc()[i][j])
						* (pointToArray[j] - getCurrentLoc()[i][j]);
			}
			leastSquareArray[i] -= (getDistances()[i]) * (getDistances()[i]);
		}

		RealMatrix jacobianMatrix = jacobianMatrix(point);
		return new Pair<RealVector, RealMatrix>(new ArrayRealVector(leastSquareArray), jacobianMatrix);
	}

}
