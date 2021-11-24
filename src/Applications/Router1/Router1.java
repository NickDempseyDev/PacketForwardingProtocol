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
			// System.out.println("i am router1 my ip's are: ");
			// InetAddress localhost = InetAddress.getLocalHost();
			// InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  			// if (allMyIps != null) {
    		// 	System.out.println(" Full list of IP addresses:");
    		// 	for (int i = 0; i < allMyIps.length; i++) {
			// 		System.out.println("    " + allMyIps[i]);
    		// 	}
  			// }
			Router router = new Router("router1", InetAddress.getByName("router1"));
			router.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}