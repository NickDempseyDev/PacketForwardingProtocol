package Router;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import Protocol.PacketHelper;

public class PacketHandler implements Runnable
{

	byte[] data;
	PacketHelper packetHelper;
	int fromPort;
	InetAddress fromIp;
	HashMap<String, InetAddress> routingTable;

	public PacketHandler(byte[] data, InetAddress fromIp, int fromPort, HashMap<String, InetAddress> routingTable)
	{
		this.data = data;
		this.fromIp = fromIp;
		this.fromPort = fromPort;
		this.routingTable = routingTable;
	}

	public void contactController()
	{
		System.out.println("Here I would contact the controller... but the controller has not yet been implemented");
	}

	public void forwardPacket()
	{
		String netId = packetHelper.getNetIdString();
		if (!routingTable.containsKey(netId))
		{
			contactController();
		}
		else
		{
			InetAddress toIp = routingTable.get(netId);
			PacketSender sender = new PacketSender(packetHelper, toIp, 51510);
			Thread t = new Thread(sender);
			t.start();
		}
	}

	@Override
	public void run()
	{
		this.packetHelper = new PacketHelper(data, data[0]);
		packetHelper.decodeRouterPacket();
		
		try 
		{
			packetHelper.createAck();
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(packetHelper.getData(), packetHelper.getData().length, fromIp, fromPort);
			System.out.println("received forwarded packet from: " + fromIp + "\nnetId: " + packetHelper.getNetIdString());
			socket.send(packet);
			socket.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
