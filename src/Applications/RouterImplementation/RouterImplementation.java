package Applications.RouterImplementation;

import java.net.InetAddress;
import java.util.HashMap;
import Router.Router;

public class RouterImplementation
{

	public static void main(String[] args)
	{
		try 
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			Router router = new Router(args[0], myIps);
			Thread.sleep(1000);
			router.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}