package Applications.Router2;

import java.net.InetAddress;
import java.util.HashMap;

import Router.Router;

public class Router2 {
	public static void main(String[] args) {
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			Router router = new Router("router2", myIps);
			Thread.sleep(10000);
			router.start();
		}
		catch (Exception e)
		{
			System.out.println(" (error retrieving server host name)");
		}
	}
}
