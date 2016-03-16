package Sim;

// This class implements a simple router

import com.sun.org.apache.xpath.internal.operations.String;
import com.sun.xml.internal.bind.v2.TODO;
import sun.nio.ch.Net;

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now = 0;
    private HomeAgent homeAgent = null;

	// When created, number of interfaces are defined


    Router(int interfaces)
    {
        _routingTable = new RouteTableEntry[interfaces];
        _interfaces = interfaces;
    }

	Router(int interfaces, HomeAgent homeAgent)
	{
		this(interfaces);
        this.homeAgent = homeAgent;
	}


	// This method connects links to the router and also informs the
	// router of the host connects to the other end of the link
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
		{
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	private SimEnt getInterface(int networkAddress)
	{
		SimEnt routerInterface=null;
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null && _routingTable[i].node() != null)
			{
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					return _routingTable[i].link();
                }
			}
            return null;
	}

    //changes given nodes interface. returns old interface if successful, 0 otherwise.
	private int switchInterface(int networkAddress, int newInterface){

        SimEnt oldLink    = null;
        SimEnt movingNode = null;
        int _oldInterface = 0;

        for(int i=0; i<_interfaces; i++){

            if (_routingTable[i] != null)
            {
                if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
                {
                    oldLink         = _routingTable[i].link();
                    movingNode      = _routingTable[i].node();
                    _oldInterface   = i;
                    SimEnt newLink  = _routingTable[newInterface].link();

                    //Set new peer for node
                    ((Node)movingNode).setPeer(newLink);

                    //remove node as peer on old link
                    ((Link)oldLink).removePeer(movingNode);

                    //Update route table
                    _routingTable[newInterface] = new RouteTableEntry(newLink, movingNode);
                    _routingTable[i]            = new RouteTableEntry(oldLink, null);

                    //send event
                    //send(movingNode, new TimerEvent(), 30);
                    return _oldInterface;

                }
            }
        }

        //If no interface match was found
        return 0;

    }

    public void printInterfaces(){
        System.out.println("-----------");
        for(int i=0; i<_interfaces; i++){
            if (_routingTable[i] != null) {
                System.out.println("Pos" + i + ": " + _routingTable[i].node() + "  |  Link: " + _routingTable[i].link());
            } else {
                System.out.println("Pos" + i + ": null" );
            }
        }
        System.out.println("-----------");
    }

    private void changeNetworkAddress(Node n, NetworkAddr newId){
        n.set_id(newId);
    }

    private void broadcastFreeInterfaces(NetworkAddr source){

        int counter = 0;

        for(int i = 0; i < _interfaces; i++){
            if (_routingTable[i] == null){
                counter++;
            }
        }

        for (int i = 0; i < _interfaces; i++){

            if (_routingTable[i] != null){
                SimEnt sendNext = _routingTable[i].link();
                send(sendNext, new RouterAdvertisement(counter), _now);
            }

        }

    }

	// When messages are received at the router this method is called
	public void recv(SimEnt source, Event event)
	{

		if (event instanceof Message)
		{
            SimEnt sendNext;
            NetworkAddr destination = ((Message) event).destination();
            NetworkAddr id = new NetworkAddr(destination.networkId(), destination.nodeId());

			System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );

            if (homeAgent == null || homeAgent.getCoa(id) == null) {
                sendNext = getInterface(((Message) event).destination().networkId());
                if (sendNext == null){
                    System.out.println("Receiver not found in table, Packet was dropped");
                    return;
                }
                System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());
            } else {
                NetworkAddr coa = homeAgent.getCoa(id);
                sendNext = getInterface(coa.networkId());
                System.out.println("HA sends to node: " + coa.networkId() + "." + coa.nodeId());
            }

            send (sendNext, event, _now);
        }

		if(event instanceof Move) {

            //Properties from node
            Node n               = ((Move) event).getNode();
            NetworkAddr _id      = n.getAddr();

            //Properties from event
            int _newInterface    = ((Move) event).getNewInterface();
            NetworkAddr _newId   = ((Move) event).getId();


            //switch interface
			int _oldInterface = switchInterface(_id.networkId(), _newInterface);

            //change to new address
            changeNetworkAddress(n, _newId);

            //Home agent
            if(homeAgent != null){
                homeAgent.registerHomeAddress(n, _id);
                homeAgent.registerNewLocation(n, _newId);
            }

            //Prints
            System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " switched to network " + _newId.networkId() + "." + _newId.nodeId());
            System.out.println("Node " + _newId.networkId() + "." + _newId.nodeId() + " switched to interface " + _newInterface + " from interface " + _oldInterface);
            printInterfaces();

        }

        if(event instanceof RouterSolicitation) {
            NetworkAddr _sourceId = ((RouterSolicitation) event).getSource();
            System.out.println("Router recived solicitation from node " + _sourceId.networkId() + "." + _sourceId.nodeId());

            broadcastFreeInterfaces(_sourceId);

            System.out.println("Router broadcasted advertisement");
        }


	}
}
