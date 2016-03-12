package Sim;

public class ConstantBitRate implements TrafficGenerator{

	
	int TimeBetweenSending;
	
	public ConstantBitRate(int TimeBetweenSending) {
		this.TimeBetweenSending = TimeBetweenSending;
	}
	
	@Override
	public int getTimeBetweenSending() {
		return TimeBetweenSending;

	}

    @Override
    public void printStatistics() {
        System.out.println("CBR: " + TimeBetweenSending);
    }

}
