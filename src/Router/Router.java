package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Protocol.PacketGenerator;

public class Router
{
	int listeningPort = 51510;
	String routerName;
	InetAddress[] myIps;
	Boolean goodToGo = false;
	PacketGenerator generator = new PacketGenerator();
	HashMap<String, String> routingTable = new HashMap<String, String>();

	public Router(String routerName, InetAddress[] myIps)
	{
		this.routerName = routerName;
		this.myIps = myIps;
	}

	// public void simulateForwarding(String netId, String msg)
	// {
	// 	PacketHelper packetHelper = new PacketHelper(netId, msg, (byte) 1);
	// 	InetAddress toIp = routingTable.get(netId);
	// 	PacketSender sender = new PacketSender(packetHelper, toIp, 51510);
	// 	Thread t = new Thread(sender);
	// 	t.start();
	// }

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
			System.out.println("MY IP(s) IS/ARE: ");
			for (InetAddress inetAddress : myIps) {
				System.out.print(inetAddress + " ");
			}
			
			System.out.println("\nSending Hello...");

			sendHello();

			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, InetAddress.getByName("0.0.0.0"));
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] dataN = new byte[packet.getLength()];
				System.arraycopy(buffer, 0, dataN, 0, dataN.length);

				PacketHandler packetHandler = new PacketHandler(dataN, packet.getAddress(), packet.getPort(), routingTable, routerName, myIps, goodToGo);
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
