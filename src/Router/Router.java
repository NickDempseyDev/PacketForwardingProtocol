package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Protocol.PacketHelper;

public class Router
{
	int listeningPort = 51510;
	String routerName;
	InetAddress myIp;
	InetAddress toIp;

	HashMap<String, InetAddress> routingTable = new HashMap<String, InetAddress>();

	public Router(String routerName, HashMap<String, InetAddress> existingRoutingTable)
	{
		this.routerName = routerName;
		this.routingTable = existingRoutingTable;
	}

	public void printName()
	{
		System.out.println("Printing from the Router Class and implemented in: " + routerName + " " + myIp);
	}

	public void simulateForwarding(String netId, String msg)
	{
		PacketHelper packetHelper = new PacketHelper(netId, msg, (byte) 1);
		InetAddress toIp = routingTable.get(netId);
		PacketSender sender = new PacketSender(packetHelper, toIp, 51510);
		Thread t = new Thread(sender);
		t.start();
	}

	public void start()
	{
		try
		{
			System.out.println("my ip: " + myIp.getHostAddress());
			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, myIp);
			while (tempBool) 
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = new byte[packet.getData().length];
				System.arraycopy(buffer, 0, data, 0, data.length);

				PacketHandler packetHandler = new PacketHandler(data, packet.getAddress(), packet.getPort(), routingTable);
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
