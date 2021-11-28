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
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			Router router = new Router("router1", myIps);
			Thread.sleep(14000);
			router.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}