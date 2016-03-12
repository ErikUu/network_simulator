package Sim;

import java.util.Random;

public class Gauss implements TrafficGenerator{

	Random r;
	
	private int MEAN;
	private int VARIANCE;
	private int gaussian;
	
	/**
	 * 
	 * @param MEAN
	 * @param VARIANCE
	 */
	public Gauss(int MEAN, int VARIANCE) {
		this.MEAN = MEAN;
		this.VARIANCE = VARIANCE;
		r = new Random();
	}

	/**
	 * @return time between seding
     */
	public int getTimeBetweenSending() {
		gaussian = getGaussian(MEAN, VARIANCE);
		return gaussian;
	}

	public void printStatistics() {
		System.out.println("Gaussian value: " + gaussian);
	}

	private int getGaussian(double aMean, double aVariance){
		return (int) (aMean + r.nextGaussian() * aVariance);
	}

}
