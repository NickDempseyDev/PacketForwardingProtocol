package Applications.EndpointSenderTest;

import java.net.InetAddress;

import EndpointRouter.EndpointRouter;

public class EndpointSenderTest
{
	public static void main(String[] args)
	{
		try
		{
			// System.out.println("i am endpoint sender and my ip's are: ");
			// InetAddress localhost = InetAddress.getLocalHost();
			// InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  			// if (allMyIps != null) {
    		// 	System.out.println(" Full list of IP addresses:");
    		// 	for (int i = 0; i < allMyIps.length; i++) {
			// 		System.out.println("    " + allMyIps[i]);
    		// 	}
  			// }
			EndpointRouter router = new EndpointRouter("EndpointRouterSender", "endpointsender", "router1");
			ApplicationTest application = new ApplicationTest(InetAddress.getLocalHost());
			Thread tRouter = new Thread(router);
			Thread tApplication = new Thread(application);
			tRouter.start();
			Thread.sleep(100);
			tApplication.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
