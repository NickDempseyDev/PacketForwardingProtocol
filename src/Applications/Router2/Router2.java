package Applications.Router2;

import java.net.InetAddress;
import java.util.HashMap;

import Router.Router;

public class Router2 {
	public static void main(String[] args) {
		try
		{
			HashMap<String, InetAddress> existingRoutingTable = new HashMap<String, InetAddress>();
			existingRoutingTable.put("tcd.scss", InetAddress.getByName("172.20.11.4"));
			Router router = new Router("router1", existingRoutingTable, InetAddress.getByName("172.20.11.2"));
			router.start();
		}
		catch (Exception e)
		{
			System.out.println(" (error retrieving server host name)");
		}
	}
}
