package Applications.EndpointReceiverTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ApplicationTest implements Runnable
{
	InetAddress myIp;
	int localRouterPort;

	public ApplicationTest(InetAddress myIp)
	{
		this.myIp = myIp;
		this.localRouterPort = 51510;
	}

	@Override
	public void run()
	{
		try
		{
			System.out.println("MY IP IS: " + myIp.getHostAddress());
			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(51511, myIp);
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = new byte[packet.getData().length];
				System.arraycopy(buffer, 0, data, 0, data.length);

				ApplicationPacketHandler packetHandler = new ApplicationPacketHandler(data, packet.getAddress(), packet.getPort());
				Thread t = new Thread(packetHandler);
				t.start();
			}
			socket.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
