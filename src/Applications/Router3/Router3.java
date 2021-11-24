package Applications.Router3;

import java.net.InetAddress;
import Router.Router;

public class Router3 {
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
			//getByName("172.20.44.3")
			Router router = new Router("router3", InetAddress.getByName("router3"));
			router.start();
		}
		catch (Exception e)
		{
			System.out.println(" (error retrieving server host name)");
		}
	}
}
