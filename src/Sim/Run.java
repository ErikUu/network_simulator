package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{

 		//Creates two links (param1: jitter range, param2: drop chance)
 		Link link1 = new Link(0, 100);
		Link link2 = new Link(0, 100);
		Link link3 = new Link(0, 100);

		
		// Create two end hosts that will be
		// communicating via the router
		Node host1 = new Node(1,1);
		Node host2 = new Node(2,1);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(10);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
        routeNode.connectInterface(5, link3, null);

        routeNode.printInterfaces();

		// Generate some traffic
		//network, node, packages, start sequence, generator(Poisson, Gauss, ConstantBitRate)
		host1.StartSending(2, 1, 5, 50, new ConstantBitRate(10));
		host2.StartSending(1, 1, 0, 50, new ConstantBitRate(20));


        //router, interface, delayed time, address to move to
		host2.move(routeNode, 2, 25, new NetworkAddr(3,1));


		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}
