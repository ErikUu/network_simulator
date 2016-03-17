package Sim;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
	private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg= 0;
	private int _seq = 0;

	public Node (int network, int node)
	{
		super();
		_id = new NetworkAddr(network, node);
	}	
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}

	public void connect(int delay, SimEnt link){
		send(this, new Connect(link), delay);
	}

	public void disconnect(int delay){
		send(this, new Disconnect(), delay);
	}
	
	public NetworkAddr getAddr()
	{
		return _id;
	}

    public void set_id(NetworkAddr _id) {
        this._id = _id;
    }

    //**********************************************************************************
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators
	
	private int _stopSendingAfter = 0; //messages
	private int _timeBetweenSending = 10; //time between messages
	private int _toNetwork = 0;
	private int _toHost = 0;
	private TrafficGenerator p;
	private PrintWriter writer;
	
	/**
	 * 
	 * @param network
	 * @param node
	 * @param number
	 * @param startSeq
	 * @param trafficGen
	 */
	public void StartSending(int network, int node, int number, int startSeq, TrafficGenerator trafficGen)
	{
		p = trafficGen;
		_stopSendingAfter = number;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		send(this, new TimerEvent(),0);
    }

    public void move(Router r, int newInterface, int delay, NetworkAddr id){
        send(r, new Move(this, newInterface, id), delay);
		send(r, new RouterSolicitation(id), delay+1);
    }



	
//**********************************************************************************	

    //vars used to implement lossy link.
    private int _received = 0;
    private double _totalDif = 0;
    private double _prevDelay = 0;
    private boolean _firstRun = true;

	// This method is called upon that an event destined for this node triggers.
	
	public void recv(SimEnt src, Event ev)
	{


		if (ev instanceof TimerEvent)
		{
			if (_stopSendingAfter > _sentmsg) {

                _timeBetweenSending = p.getTimeBetweenSending();

				_sentmsg++;
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				fileWrite(_timeBetweenSending);
				send(this, new TimerEvent(),_timeBetweenSending);
                Sink.nodeTimeEventRecived(this);
                System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
                _seq++;

				p.printStatistics();



			}
		}
		if (ev instanceof Message)
		{
			double _currentDelay = 0;
			double jitter = 0;
			
			if(_firstRun){
				_currentDelay = SimEngine.getTime();
				_firstRun = false;
			} else {
				_currentDelay = SimEngine.getTime();
			}


			//calculate  difference in delay
			double diffPrevCurrent = Math.abs(_currentDelay - _prevDelay);
			
			//adds to the total delay difference
			_totalDif += diffPrevCurrent;
			
			//calculate jitter
			jitter = (_received < 1 ) ? _totalDif : _totalDif/(_received);
			
			_received++;
			
			//sets previous delay to current delay
			_prevDelay = _currentDelay;
			
			//System.out.println("\ndiff:   "+diffPrevCurrent);
			//System.out.println("Node:"+_id.networkId() +", JITTER:    totalDif:"+_totalDif + " / recived:" + (_received-1) + " = " + jitter+"\n");
			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
			
		}

		if(ev instanceof RouterAdvertisement){
			int freeInterfaces = ((RouterAdvertisement) ev).getFreeInterfaces();
			System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " received router advertisement, " + freeInterfaces + " free interfaces.");
		}

		if (ev instanceof Connect) {
			if (_peer == null){
				setPeer( ((Connect) ev).getLink() );
				System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " CONNECTED to link");
			}
		}

		if (ev instanceof Disconnect) {
			if (_peer != null) {
				((Link)_peer).removePeer(this);
				_peer = null;
				System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " DISCONNECTED from link");
			}
		}


	}
	
	void fileWrite(double n){

		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("time_between-sending.txt", true)))) {
		    out.println(String.valueOf(n ));
		}catch (IOException e) {
		   System.out.println(e);
		}
		
	}

	
}
