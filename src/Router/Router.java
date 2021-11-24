package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Router
{
	int listeningPort = 51510;
	String routerName;
	InetAddress myIp;

	HashMap<String, String> routingTable = new HashMap<String, String>();

	public Router(String routerName, InetAddress myIp)
	{
		this.routerName = routerName;
		this.myIp = myIp;
	}

	public void printName()
	{
		System.out.println("Printing from the Router Class and implemented in: " + routerName + " " + myIp);
	}

	// public void simulateForwarding(String netId, String msg)
	// {
	// 	PacketHelper packetHelper = new PacketHelper(netId, msg, (byte) 1);
	// 	InetAddress toIp = routingTable.get(netId);
	// 	PacketSender sender = new PacketSender(packetHelper, toIp, 51510);
	// 	Thread t = new Thread(sender);
	// 	t.start();
	// }

	public void start()
	{
		try
		{
			System.out.println("MY IP IS: " + myIp);
			boolean tempBool = true;
			DatagramSocket socket = new DatagramSocket(listeningPort, InetAddress.getByName("0.0.0.0"));
			while (tempBool)
			{
				byte[] buffer = new byte[1500];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] dataN = new byte[packet.getLength()];
				System.arraycopy(buffer, 0, dataN, 0, dataN.length);

				PacketHandler packetHandler = new PacketHandler(dataN, packet.getAddress(), packet.getPort(), routingTable, routerName);
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
