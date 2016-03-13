package Sim;

import java.text.DecimalFormat;

// This class implements a link without any loss, jitter or delay

public class Link extends SimEnt{
	private SimEnt _connectorA=null;
	private SimEnt _connectorB=null;
	private int _now=0;
	private int delayRange;
	private int dropRatio;

	
	public Link()
	{
		super();
	}
	
	
	public Link(int delayRange, int dropRatio)
	{
		super();
		this.delayRange = delayRange;
		this.dropRatio = dropRatio;
	}
	
	
	// Connects the link to some simulation entity like
	// a node, switch, router etc.
	
	public void setConnector(SimEnt connectTo)
	{
		if (_connectorA == null) 
			_connectorA=connectTo;
		else
			_connectorB=connectTo;
	}

	// Called when a message enters the link
	
	public void recv(SimEnt src, Event ev) {

		if ((_connectorA != null && _connectorB != null) && isDropped(dropRatio)) {
			System.out.println("Link recv msg, passes it through");

			if (src == _connectorA)
				send(_connectorB, ev, _now+randomDelay(delayRange));
			else
				send(_connectorA, ev, _now+randomDelay(delayRange));	

		} else {
			System.out.println("packet was dropped");
		}

	}
	
	//Returns true/false depending on given chance
	private boolean isDropped(double chance){
		return (Math.random()<=chance/100) ? true : false;
	}
	
	//calculates random delay between 0 - range
	private double randomDelay(int range){
		int r = (int) (Math.random() * (range - 0)) + 0;
		System.out.println("Link delay:      " + r);
		return r;
	}
	
	
	
}