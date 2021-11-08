package Applications.EndpointReceiverTest;

import java.net.InetAddress;

import EndpointRouter.EndpointRouter;

public class EndpointReceiverTest
{
	public static void main(String[] args)
	{
		try
		{
			// System.out.println("i am endpoint receiver and my ip's are: ");
			// InetAddress localhost = InetAddress.getLocalHost();
			// InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
  			// if (allMyIps != null) {
    		// 	System.out.println(" Full list of IP addresses:");
    		// 	for (int i = 0; i < allMyIps.length; i++) {
			// 		System.out.println("    " + allMyIps[i]);
    		// 	}
  			// }
			InetAddress nextRouter = InetAddress.getLocalHost();
			EndpointRouter router = new EndpointRouter("EndpointRouterReceiver", InetAddress.getLocalHost(), nextRouter);
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
