package Applications.Router2;

import java.net.InetAddress;
import java.util.HashMap;

import Router.Router;

public class Router2 {
	public static void main(String[] args) {
		try
		{
			// System.out.println("i am router2 and my ip's are: ");
			// InetAddress localhost = InetAddress.getLocalHost();
			// InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  			// if (allMyIps != null) {
    		// 	System.out.println(" Full list of IP addresses:");
    		// 	for (int i = 0; i < allMyIps.length; i++) {
			// 		System.out.println("    " + allMyIps[i]);
    		// 	}
  			// }
			HashMap<String, InetAddress> existingRoutingTable = new HashMap<String, InetAddress>();
			existingRoutingTable.put("tcd.scss", InetAddress.getByName("172.20.44.2"));
			Router router = new Router("router2", existingRoutingTable, InetAddress.getByName("172.20.44.3"));
			router.start();
		}
		catch (Exception e)
		{
			System.out.println(" (error retrieving server host name)");
		}
	}
}
