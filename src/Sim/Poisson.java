package Sim;

public class Poisson implements TrafficGenerator{
	
	private double lambda;
	private int poisson;
	
	/**
	 * 
	 * @param lambda
	 */
	Poisson(double lambda){
		this.lambda = lambda;
	}
	
	public int getTimeBetweenSending(){

	    double L = Math.exp(-lambda);
	    double p = 1.0;
	    int k = 0;

	    do {
	      k++;
	      p *= Math.random();
	      
	    } while (p > L);

		poisson = k - 1;

	    return poisson;
	}


	public void printStatistics() {
		System.out.println("Poisson: " + poisson);
	}

}
