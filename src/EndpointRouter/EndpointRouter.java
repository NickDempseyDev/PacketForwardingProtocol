package EndpointRouter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Protocol.PacketGenerator;

public class EndpointRouter implements Runnable
{
	int listeningPort = 51510;
	String routerName;
	InetAddress[] myIps;
	String nextRouter;
	Boolean goodToGo = false;
	PacketGenerator generator = new PacketGenerator();

	public EndpointRouter(String routerName, InetAddress[] myIps, String nextRouter)
	{
		this.routerName = routerName;
		this.myIps = myIps;
		this.nextRouter = nextRouter;
	}

	public void sendHello()
	{
		try
		{
			DatagramSocket socket = new DatagramSocket();
			byte[] bytes = generator.createHelloPacket(routerName, myIps);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("controller"), 51510);
			socket.send(packet);
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void start()
	{
		try
		{
			System.out.println("\nSending Hello...");

			sendHello();

			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, InetAddress.getByName(routerName));
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(buffer, 0, data, 0, data.length);
				int nextPort;
				if (packet.getData()[0] == (byte) 1)
				{
					nextPort = 51511;
				}
				else
				{
					nextPort = 51510;
				}

				PacketHandler packetHandler = new PacketHandler(this, nextRouter, nextPort, packet);
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
