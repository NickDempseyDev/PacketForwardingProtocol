package Applications.EndpointReceiverTest;

import java.net.InetAddress;

import EndpointRouter.EndpointRouter;

public class EndpointReceiverTest
{
	public static void main(String[] args)
	{
		try
		{
			InetAddress localhost = InetAddress.getLocalHost();
			InetAddress[] myIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			EndpointRouter router = new EndpointRouter("endpointreceiver", myIps, "router3");
			ApplicationTest application = new ApplicationTest(InetAddress.getLocalHost());
			Thread tRouter = new Thread(router);
			Thread tApplication = new Thread(application);
			tRouter.start();
			Thread.sleep(1000);
			tApplication.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
