package Applications.Router3;

import java.net.InetAddress;
import Router.Router;

public class Router3 {
	public static void main(String[] args) {
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			Router router = new Router("router3", myIps);
			router.start();
		}
		catch (Exception e)
		{
			System.out.println(" (error retrieving server host name)");
		}
	}
}
