package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EndpointRouter implements Runnable
{
	int listeningPort = 51510;
	String routerName;
	InetAddress myIp;

	public EndpointRouter(String routerName, InetAddress myIp)
	{
		this.routerName = routerName;
		this.myIp = myIp;
	}

	public void start()
	{
		try
		{
			System.out.println("MY IP IS: " + myIp.getHostAddress());
			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, myIp);
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = new byte[packet.getData().length];
				System.arraycopy(buffer, 0, data, 0, data.length);

				PacketHandler packetHandler = new PacketHandler(data, packet.getAddress(), packet.getPort(), myIp);
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

	}
}
