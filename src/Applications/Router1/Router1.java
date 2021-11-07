package Applications.Router1;

import java.net.InetAddress;
import java.util.HashMap;
import Router.Router;

public class Router1
{

	public static void main(String[] args)
	{
		try 
		{
			HashMap<String, InetAddress> existingRoutingTable = new HashMap<String, InetAddress>();
			existingRoutingTable.put("tcd.scss", InetAddress.getByName("172.20.11.3"));
			Router router = new Router("router1", existingRoutingTable);
			router.simulateForwarding("tcd.scss", "This is a test payload simulated by router 1");
			router.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}