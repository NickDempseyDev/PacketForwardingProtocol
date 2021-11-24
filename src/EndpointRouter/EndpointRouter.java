package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EndpointRouter implements Runnable
{
	int listeningPort = 51510;
	String routerName;
	String myIp;
	String nextRouter;

	public EndpointRouter(String routerName, String myIp, String nextRouter)
	{
		this.routerName = routerName;
		this.myIp = myIp;
		this.nextRouter = nextRouter;
	}

	public void start()
	{
		try
		{
			System.out.println("MY IP IS: " + InetAddress.getByName(myIp));
			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, InetAddress.getByName(myIp));
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(buffer, 0, data, 0, data.length);
				int nextPort;
				if (data[0] == (byte) 1)
				{
					nextPort = 51511;
				}
				else
				{
					nextPort = 51510;
				}

				PacketHandler packetHandler = new PacketHandler(data, packet.getAddress(), packet.getPort(), nextRouter, nextPort);
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

	@Override
	public void run()
	{
		start();
	}
}
