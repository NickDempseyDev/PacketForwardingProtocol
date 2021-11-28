package Applications.EndpointSenderTest;

import java.net.InetAddress;

import EndpointRouter.EndpointRouter;

public class EndpointSenderTest
{
	public static void main(String[] args)
	{
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			EndpointRouter router = new EndpointRouter("endpointsender", myIps, "router1");
			ApplicationTest application = new ApplicationTest(InetAddress.getLocalHost());
			Thread tRouter = new Thread(router);
			Thread tApplication = new Thread(application);
			tRouter.start();
			Thread.sleep(18000);
			// tApplication.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}